package com.mmall.dao;

import com.mmall.pojo.Cart;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CartMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    Cart selectByUserIdAndProductId(@Param("userId")Integer userId, @Param("productId")Integer productId);

    List<Cart> selectCartListByUserId(Integer usreId);

    int selectCartProductCheckedStatus(Integer userId);

    int deleteByUserIdAndProducts(@Param("userId")Integer userId,@Param("productIds")List<String> productIds);

    int checkedOrUnCheckedProduct(@Param("userId")Integer userId,@Param("productId")Integer ProductId, @Param("checked") Integer checked);

    int selectCartProductCount(Integer userId);

    List<Cart> selectCheckedCartByUserId(Integer userId);
}