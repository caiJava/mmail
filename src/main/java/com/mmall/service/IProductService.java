package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.vo.ProductDetailVo;

/**
 * Created by Administrator on 2019/3/13/013.
 */
public interface IProductService {
    ServerResponse saveProduct(Product product);

    public ServerResponse setSaleStatus(Integer productId, Integer status);

    ServerResponse<ProductDetailVo> managerProductDetail(Integer productId);

    ServerResponse<PageInfo> getList(Integer pageNum, Integer pageSize);

    ServerResponse<PageInfo> productSeacher(String productName, Integer productId, Integer pageNum, Integer pageSize);

    ServerResponse<ProductDetailVo> detail(Integer productId);

    ServerResponse<PageInfo> getProductByKeyworkCategory(String keywork, Integer categoryId,
                                                         Integer pageNum, Integer pageSize, String orderBy);

}
