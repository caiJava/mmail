package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Shipping;

import java.util.Map;

/**
 * Created by Administrator on 2019/3/17/017.
 */
public interface IShippingService {

    ServerResponse<Map<String,Integer>> add(Integer userId, Shipping shipping);

    ServerResponse delete(Integer userid, Integer shippingId);

    ServerResponse update(Integer userId, Shipping shipping);

    ServerResponse<Shipping> select(Integer userId, Integer shippingId);

    ServerResponse<PageInfo> list(Integer userId, Integer pageNum, Integer pageSize);
}
