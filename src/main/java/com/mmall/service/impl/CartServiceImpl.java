package com.mmall.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CartMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Product;
import com.mmall.service.ICartService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.CartProductVo;
import com.mmall.vo.CartVo;
import net.sf.jsqlparser.schema.Server;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Administrator on 2019/3/16/016.
 */
@Service("iCartService")
public class CartServiceImpl implements ICartService{

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    /**
     * 向购物车中加入商品
     * @param userId
     * @param count
     * @param productId
     * @return
     */
    @Override
    public ServerResponse<CartVo> add(Integer userId, Integer count, Integer productId) {
        if (count == null || productId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Cart cart = cartMapper.selectByUserIdAndProductId(userId, productId);
        if (cart == null) {
            //说明购物车里没有这个商品，需要加入购物车
            cart = new Cart();
            cart.setQuantity(count);
            cart.setUserId(userId);
            cart.setProductId(productId);
            cart.setChecked(Const.Cart.CHACKED);
            cartMapper.insert(cart);
        } else {
            //说明商品已经在购物车里了，增加一下数量就可以了
            cart.setQuantity(cart.getQuantity() + 1);
            cart.setChecked(Const.Cart.CHACKED);
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        return this.list(userId);
    }

        /**
         * 更新购物车的方法
         * @param userId
         * @param count
         * @param productId
         * @return
         */
    @Override
    public ServerResponse<CartVo> update(Integer userId, Integer count, Integer productId){
        if(count == null || productId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Cart cart = cartMapper.selectByUserIdAndProductId(userId, productId);
        if(cart != null){
            cart.setQuantity(count);
        }
        cartMapper.updateByPrimaryKeySelective(cart);
        return this.list(userId);
    }

    /**
     *  在购物车中删除指定部品
     * @param userId
     * @param productIds 要删除的商品id集合，用逗号隔开
     * @return
     */
    @Override
    public ServerResponse<CartVo> delete(Integer userId, String productIds){
        if(userId == null || productIds == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        //用逗号分割成一个list
        List<String> productIdList = Splitter.on(",").splitToList(productIds);
        if(CollectionUtils.isEmpty(productIdList)){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        cartMapper.deleteByUserIdAndProducts(userId,productIdList);
        return this.list(userId);
    }

    /**
     * 查询购物车中所有商品
     * @param userId
     * @return
     */
    @Override
    public ServerResponse<CartVo> list(Integer userId){
        if(userId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        CartVo cartVo = this.getCartVoLimit(userId);
        return ServerResponse.createBySuccess(cartVo);
    }

    /**
     * 选择购物车里的商品
     * @param userId        用户id
     * @param productId     要勾选商品的id,如果为null,则是全选或全不选
     * @param checked       勾选 or 取消勾选
     * @return
     */
    @Override
    public ServerResponse<CartVo> selectOrUnSelect(Integer userId, Integer productId, Integer checked){
        if(userId == null || checked == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        cartMapper.checkedOrUnCheckedProduct(userId, productId, checked);
        return this.list(userId);
    }

    /**
     * 查询购物车中商品的总数
     * @param userId
     * @return
     */
    @Override
    public ServerResponse<Integer> getCartProductCount(Integer userId){
        if(userId == null){
            return ServerResponse.createBySuccess(0);
        }
        return ServerResponse.createBySuccess(cartMapper.selectCartProductCount(userId)); //todo
    }
    /**
     * 包装CartVo的方法
     * @param userId
     * @return
     */
    private CartVo getCartVoLimit(Integer userId) {
        CartVo cartVo = new CartVo();
        List<Cart> cartList = cartMapper.selectCartListByUserId(userId);
        List<CartProductVo> cartProductVoList = Lists.newArrayList();
        BigDecimal cartTotalPrice = new BigDecimal("0");
        for (Cart cartItem : cartList) {
            CartProductVo cartProductVo = new CartProductVo();
            cartProductVo.setId(cartItem.getId());
            cartProductVo.setUserId(cartItem.getUserId());
            cartProductVo.setProductId(cartItem.getProductId());
            cartProductVo.setQuantity(cartItem.getQuantity());
            Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
            if (product != null) {
                cartProductVo.setProductMainImage(product.getMainImage());
                cartProductVo.setProductName(product.getName());
                cartProductVo.setProductStatus(product.getStatus());
                cartProductVo.setProductSubtitle(product.getSubtitle());
                cartProductVo.setProductPrice(product.getPrice());
                cartProductVo.setProductStock(product.getStock());
                cartProductVo.setProductChecked(cartItem.getChecked());
                int buyLimitCount = 0;
                //判断库存  实际库存数量  >= 用户要购买的数量
                if (product.getStock() >= cartItem.getQuantity()) {
                    //库存充足
                    buyLimitCount = cartItem.getQuantity();
                    cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                } else {
                    //库存不足
                    buyLimitCount = product.getStock();
                    cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
                    //要更新一下数据库中的购物车
                    Cart cart = new Cart();
                    cart.setId(cartItem.getId());
                    cart.setQuantity(buyLimitCount);
                    cartMapper.updateByPrimaryKeySelective(cart);
                }
                cartProductVo.setQuantity(buyLimitCount);
                //计算该部品总价
                cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(buyLimitCount, product.getPrice().doubleValue()));
                cartProductVo.setProductChecked(cartItem.getChecked());
                //如果该商品已经勾选，则加入购物车总价
                if(cartProductVo.getProductChecked() == Const.Cart.CHACKED) {
                    cartTotalPrice = BigDecimalUtil.add(cartProductVo.getProductPrice().doubleValue(),cartTotalPrice.doubleValue());
                }
            }else{
                cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
            }
            cartProductVoList.add(cartProductVo);
        }
        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setCartTotalPrice(cartTotalPrice);
        //是否全选
        cartVo.setAllChecked(this.getAllProductChecked(userId));
        cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        return cartVo;
    }

    /**
     * 判断购物车是否全选了
     * @param userId
     * @return
     */
    private boolean getAllProductChecked(Integer userId) {
        if(userId == null){
            return false;
        }
        return cartMapper.selectCartProductCheckedStatus(userId) == 0;
    }
}
