package com.mmall.controller.portal;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Product;
import com.mmall.service.IProductService;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Administrator on 2019/3/15/015.
 */
@Controller
@RequestMapping("/product/")
public class ProductController {

    @Autowired
    IProductService iProductService;

    @Autowired
    ProductMapper productMapper;

    /**
     * 根据id查询商品底细信息
     * @param productId
     * @return
     */
    @RequestMapping("/detail.do")
    @ResponseBody
    public ServerResponse<ProductDetailVo> detail(Integer productId){
        return iProductService.detail(productId);

    }

    @RequestMapping("/list.do")
    @ResponseBody
    public ServerResponse<PageInfo> list(@RequestParam(value = "productName", required = false)String productName,
                                         @RequestParam(value = "categoryId",required = false)Integer categoryId,
                                         @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                         @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                         @RequestParam(value = "orderBy", defaultValue = "") String orderBy){
        return iProductService.getProductByKeyworkCategory(productName,categoryId,pageNum,pageSize,orderBy);
    }














}
