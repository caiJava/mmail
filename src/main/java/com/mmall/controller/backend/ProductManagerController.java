package com.mmall.controller.backend;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IFileService;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import net.sf.jsqlparser.schema.Server;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.Map;

/**
 * Created by Administrator on 2019/3/13/013.
 */
@Controller
@RequestMapping(value = "manager/product", method = RequestMethod.POST)
public class ProductManagerController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private IProductService iProductService;

    @Autowired
    IFileService iFileService;
    /**
     * 新增产品或修改产品
     * @param session
     * @param product
     * @return
     */
    @RequestMapping("save.do")
    @ResponseBody
    public ServerResponse saveProduct(HttpSession session, Product product){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"请重新登陆");
        }
        //判断是不是管理员
        if(iUserService.checkIsAdmin(user).isSuccess()){
            return iProductService.saveProduct(product);
        }else{
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    /**
     * 修改产品销售状态
     * @param session
     * @param productId
     * @param status
     * @return
     */
    @RequestMapping(value = "set_sale_status.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse setSaleStatus(HttpSession session, Integer productId, Integer status){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"请重新登陆");
        }
        //判断是不是管理员
        if(iUserService.checkIsAdmin(user).isSuccess()){
            return iProductService.setSaleStatus(productId,status);
        }else{
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    /**
     * 获取商品信息，返回vo对象
     * @param session
     * @param productId
     * @return
     */
    @RequestMapping(value = "get_detail.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<ProductDetailVo> getDetail(HttpSession session, Integer productId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"请重新登陆");
        }
        //判断是不是管理员
        if (iUserService.checkIsAdmin(user).isSuccess()) {
            return iProductService.managerProductDetail(productId);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    /**
     * 获取商品list,分页
     * @param session
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "get_list.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<PageInfo> getList(HttpSession session,
                                            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"请重新登陆");
        }
        //判断是不是管理员
        if (iUserService.checkIsAdmin(user).isSuccess()) {
            return iProductService.getList(pageNum, pageSize);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    /**
     *  根据商品名和商品id查询商品
     * @param session
     * @param productName
     * @param productId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "search.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<PageInfo> productSeacher(HttpSession session, String productName, Integer productId,
                                                   @RequestParam(value = "pageNum", defaultValue = "1")Integer pageNum,
                                                    @RequestParam(value = "pageSize", defaultValue = "10")Integer pageSize){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"请重新登陆");
        }
        //判断是不是管理员
        if (iUserService.checkIsAdmin(user).isSuccess()) {
            return iProductService.productSeacher(productName, productId, pageNum, pageSize);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    /**
     * 上传文件
     * @param session
     * @param request
     * @param file
     * @return
     */
    @RequestMapping(value = "upload.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map> upload(HttpSession session, HttpServletRequest request,
                                          @RequestParam(value = "upload_file", required = false) MultipartFile file){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"请重新登陆");
        }
        //判断是不是管理员
        if (iUserService.checkIsAdmin(user).isSuccess()) {
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file,path);

            if(StringUtils.isBlank(targetFileName)){
                return ServerResponse.createByErrorMessage("上传文件失败");
            }

            String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;
            Map map = Maps.newHashMap();
            map.put("uri",targetFileName);
            map.put("url",url);
            return ServerResponse.createBySuccess(map);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    /**
     * 上传富文本文件，富文本对后台的返回值有一定要求
     * @param session
     * @param request
     * @param file
     * @return
     */
    @RequestMapping(value = "richtext_img_upload.do", method = RequestMethod.POST)
    @ResponseBody
    public Map richtextImgUpload(HttpSession session, HttpServletRequest request,HttpServletResponse response,
                                          @RequestParam(value = "upload_file", required = false) MultipartFile file){
        Map resultMap = Maps.newHashMap();
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            resultMap.put("success",false);
            resultMap.put("msg","请重新登陆");
        }
        //判断是不是管理员
        if (iUserService.checkIsAdmin(user).isSuccess()) {
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file,path);
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;
            if(StringUtils.isBlank(targetFileName)){
                resultMap.put("success",false);
                resultMap.put("msg","上传文件失败");
            }else{
                resultMap.put("success",true);
                resultMap.put("msg","上传文件成功");
                resultMap.put("file_path",url);
                response.addHeader("Access-control-Allow-Headers","X-File-Name");     //上传文件成功要加入头，插件要求的
            }
        } else {
            resultMap.put("success",false);
            resultMap.put("msg","不是管理员，无权限操作");
        }
        return resultMap;
    }
}
