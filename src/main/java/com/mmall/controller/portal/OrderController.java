package com.mmall.controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Shipping;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.ws.Response;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Administrator on 2019/3/18/018.
 */
@Controller
@RequestMapping("/order/")
public class OrderController {

    private Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private IOrderService iOrderService;

    public ServerResponse create(HttpSession session, Integer shippingId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc());
        }
    }

    /**
     * 支付接品,下单
     * @param session
     * @param orderNo    订单号
     * @param request
     * @return
     */
    @RequestMapping(value = "pay.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse pay(HttpSession session, Long orderNo, HttpServletRequest request){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc());
        }
        String path = request.getSession().getServletContext().getRealPath("upload");
        return iOrderService.pay(orderNo,user.getId(),path);
    }

    /**
     *  阿里回调接口
     */
    @RequestMapping(value = "alipay_callback.do", method = RequestMethod.POST)
    @ResponseBody
    public Object alipayCallback(HttpServletRequest request){
        Map<String,String> param = Maps.newHashMap();
        Map requestParams = request.getParameterMap();
        for(Iterator iter = requestParams.keySet().iterator(); iter.hasNext(); ){
            String name = (String) iter.next();
            String[] value = (String[])requestParams.get(name);
            String valueStr = "";
            for(int i = 0; i < value.length; i ++){
                valueStr = ( i ==  value.length -1 ) ? valueStr + value[i] : valueStr + value[i] + ",";
            }
            param.put(name,valueStr);
        }
        logger.info("支付宝回调,sign:{},trade_status:{},参数:{}",param.get("sign"),param.get("trade_status"),param.toString());
        //在通知返回参数列表中，除去sign、sign_type两个参数外，凡是通知返回回来的参数皆是待验签的参数。
        param.remove("sign_type");
        param.remove("sign");
        try {
            boolean alipayRSACheckedV2 = AlipaySignature.rsaCheckV2(param, Configs.getAlipayPublicKey(),
                                                                    "utf-8",Configs.getSignType());
            if(!alipayRSACheckedV2){
                logger.error("支付宝回调异常，验证不通过",);
                return ServerResponse.createByErrorMessage("支付宝回调异常，验证不通过");
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        //业务
        ServerResponse serverResponse = iOrderService.alipayCallback(param);
        if(serverResponse.isSuccess()){
            return Const.AlipayCallback.RESPONSE_SUCCESS;
        }
        return Const.AlipayCallback.RESPONSE_FAILED;
    }

    /**
     * 查询订单状态接口
     * @param session
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "query_order_pay_status", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Boolean> queryOrderPayStatus(HttpSession session, Long orderNo){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc());
        }
        ServerResponse serverResponse = iOrderService.queryOrderPayStatus(user.getId(), orderNo);
        if(serverResponse.isSuccess()){
            return ServerResponse.createBySuccess(true);
        }
        return ServerResponse.createBySuccess(false);
    }


}
