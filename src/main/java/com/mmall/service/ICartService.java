package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.vo.CartVo;

/**
 * Created by Administrator on 2019/3/16/016.
 */
public interface ICartService {

    ServerResponse<CartVo> add(Integer userId, Integer count, Integer productId);

    ServerResponse<CartVo> update(Integer userId, Integer count, Integer productId);

    ServerResponse<CartVo> delete(Integer userId, String productIds);

    ServerResponse<CartVo> list(Integer userId);

    ServerResponse<CartVo> selectOrUnSelect(Integer userId, Integer productId, Integer checked);

    ServerResponse<Integer> getCartProductCount(Integer userId);
}
