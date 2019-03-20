package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.ShippingMapper;
import com.mmall.pojo.Shipping;
import com.mmall.service.IShippingService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2019/3/17/017.
 */
@Service("iShippingService")
public class ShippingServiceImpl implements IShippingService{

    @Autowired
    private ShippingMapper shippingMapper;

    /**
     * 添加收货地址
     * @param userId    用户id
     * @param shipping  收货地址对象
     * @return          返回收货地址在db中的id
     */
    @Override
    public ServerResponse<Map<String,Integer>> add(Integer userId, Shipping shipping){
        if(userId == null || shipping == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                    ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Integer rowCount = shippingMapper.insert(shipping);
        if(rowCount > 0){
            Map<String,Integer> resultMap = Maps.newHashMap();
            resultMap.put("shippingId",rowCount);
            return ServerResponse.createBySuccess("添加地址成功",resultMap);
        }
        return ServerResponse.createByErrorMessage("添加地址失败");
    }

    /**
     * 删除收货地址,
     * @param userId        用户id
     * @param shippingId    收货地址id
     * @return
     */
    @Override
    public ServerResponse delete(Integer userId, Integer shippingId){
        if(userId == null || shippingId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                    ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        //传入用户id和收货地址id,防止横向越权
        int rowCount = shippingMapper.deleteByUserIdAndShippingId(userId, shippingId);
        if(rowCount > 0){
            return ServerResponse.createBySuccess("删除地址成功");
        }
        return ServerResponse.createByErrorMessage("删除地址失败");
    }

    /**
     * 更新收货地址
     * @param userId
     * @param shipping
     * @return
     */
    @Override
    public ServerResponse update(Integer userId, Shipping shipping){
        if(userId == null || shipping == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                    ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        //根据用户id和收货地址id，要重新setter一个用户id,防止横向越权
        shipping.setUserId(userId);
        int rowCount = shippingMapper.updateByUserId(shipping);
        if(rowCount > 0){
            return ServerResponse.createBySuccessMessage("更新地址成功");
        }
        return ServerResponse.createByErrorMessage("更新地址失败");
    }

    /**
     * 在db中查找收货地址的详细信息
     * @param userId
     * @param shippingId
     * @return
     */
    @Override
    public ServerResponse<Shipping> select(Integer userId, Integer shippingId){
        if(userId == null || shippingId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                    ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Shipping shipping = shippingMapper.selectByUserIdAndShippingId(userId,shippingId);
        if(shipping != null){
            return ServerResponse.createBySuccess(shipping);
        }
        return ServerResponse.createByErrorMessage("查不到该地址");
    }

    /**
     * 查询所有收货地址
     * @param userId     用户id
     * @param pageNum    页码
     * @param pageSize   每页显示多少条
     * @return
     */
    @Override
    public ServerResponse<PageInfo> list(Integer userId, Integer pageNum, Integer pageSize){
        if(userId == null || pageNum == null || pageSize == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                    ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        PageHelper.startPage(pageNum, pageSize);
        List<Shipping> shippingList = shippingMapper.selectByUserId(userId);
        PageInfo pageInfo = new PageInfo(shippingList);
        return ServerResponse.createBySuccess(pageInfo);
    }





}
