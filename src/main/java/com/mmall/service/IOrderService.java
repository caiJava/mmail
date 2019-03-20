package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.vo.OrderVo;

import java.util.Map;

/**
 * Created by Administrator on 2019/3/18/018.
 */
public interface IOrderService {

    ServerResponse<Map<String,String>> pay(Long orderNo, Integer userId, String path);

    ServerResponse alipayCallback(Map<String,String> param);

    ServerResponse queryOrderPayStatus(Integer userId, Long orderNo);

    ServerResponse<OrderVo> createOrder(Integer userId, Integer shippingId);

    ServerResponse cancel(Integer userId, Long orderId);
}
