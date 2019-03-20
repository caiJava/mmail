package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.ICategoryService;
import com.mmall.service.IProductService;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * Created by Administrator on 2019/3/13/013.
 */
@Service("iProductService")
public class PorductServiceImpl implements IProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ICategoryService iCategoryService;
    /**
     * 新增产品或修改产品
     * @param product
     * @return
     */
    @Override
    public ServerResponse saveProduct(Product product){
        if(product != null){
            if(StringUtils.isNotBlank(product.getSubImages())){
                String[] subImageArray = product.getSubImages().split(",");
                if(subImageArray.length > 0){
                    product.setMainImage(subImageArray[0]);
                }
            }
            if(product.getId() != null){
                int rowCount = productMapper.updateByPrimaryKeySelective(product);
                if(rowCount > 0){
                    return ServerResponse.createBySuccess("更新产品成功");
                }
                return ServerResponse.createByErrorMessage("更新产品失败");
            }else{
                int rowCount = productMapper.insertSelective(product);
                if(rowCount > 0){
                    return ServerResponse.createBySuccess("新增产品成功");
                }
                return ServerResponse.createByErrorMessage("新增产品失败");
            }
        }
        return ServerResponse.createByErrorMessage("操作产品失败");
    }

    /**
     * 更新产品销售状态
     * @param productId
     * @param status
     * @return
     */
    @Override
    public ServerResponse setSaleStatus(Integer productId, Integer status){
        if(productId == null || status == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);

        int rowCount = productMapper.updateByPrimaryKeySelective(product);
        if(rowCount > 0){
            return ServerResponse.createBySuccessMessage("更新产品销售状态成功");
        }
        return ServerResponse.createByErrorMessage("更新产品销售状态失败");
    }

    /**
     * 获取商品详细信息
     * @param productId
     * @return
     */
    @Override
    public ServerResponse<ProductDetailVo> managerProductDetail(Integer productId){
        if(productId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if(product == null){
            return ServerResponse.createByErrorMessage("商品不存在或已删除");
        }
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);
    }

    /**
     * 生成vo对象的方法
     * @param product
     * @return
     */
    public ProductDetailVo assembleProductDetailVo(Product product){
        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImages(product.getSubImages());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setName(product.getName());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setStock(product.getStock());
        //图片服务器前缀
        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));
        //设置商品父类id
        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if(category == null){
            productDetailVo.setParentCategoryId(0);
        }else{
            productDetailVo.setParentCategoryId(category.getParentId());
        }

        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));

        return productDetailVo;
    }

    /**
     * 查询商品列表，使用了mybatis的分页插件
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public ServerResponse<PageInfo> getList(Integer pageNum, Integer pageSize){
        if(pageNum == null || pageSize == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        PageHelper.startPage(pageNum,pageSize);
        List<Product> list = productMapper.selectList();
        List<ProductListVo> voList = Lists.newArrayList();
        for(Product productItem : list){
            voList.add(assembleProductListVo(productItem));
        }
        PageInfo pageInfo = new PageInfo(list);
        pageInfo.setList(voList);
        return ServerResponse.createBySuccess(pageInfo);
    }

    /**
     * 生成ProductListVo 的方法
     * @param product
     * @return
     */
    public ProductListVo assembleProductListVo(Product product){
        ProductListVo productListVo = new ProductListVo();
        productListVo.setId(product.getId());
        productListVo.setName(product.getName());
        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setPrice(product.getPrice());
        productListVo.setStatus(product.getStatus());
        productListVo.setSubtitle(product.getSubtitle());
        return productListVo;
    }

    /**
     *  根据商品名和商品id查询商品
     * @param productName 要查询的商品名
     * @param productId   要查询的商品id
     * @param pageNum     第几页
     * @param pageSize    一页显示几个商品
     * @return
     */
    @Override
    public ServerResponse<PageInfo> productSeacher(String productName, Integer productId, Integer pageNum, Integer pageSize){
        PageHelper.startPage(pageNum,pageSize);
        //如果商品名字不为空，在前后拼上%
        if(StringUtils.isNotBlank(productName)){
            productName = new StringBuilder().append("%").append(productName).append("%").toString();
        }
        List<Product> productList = productMapper.selectByNameAndId(productName, productId);
        List<ProductListVo> voList = Lists.newArrayList();
        for(Product productItem : productList){
            voList.add(assembleProductListVo(productItem));
        }
        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(voList);
        return ServerResponse.createBySuccess(pageInfo);
    }

    /**
     * 根据商品id查询商品详细信息
     * @param productId
     * @return
     */
    @Override
    public ServerResponse<ProductDetailVo> detail(Integer productId){
        if(productId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if(product == null){
            return ServerResponse.createByErrorMessage("商品已下架或已删除");
        }
        if(product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode()){
            return ServerResponse.createByErrorMessage("商品已下架或已删除");
        }
        ProductDetailVo productDetailVo = this.assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);
    }

    /**
     * 根据关键字和分类查询商品
     * @param keywork       商品关键字
     * @param categoryId    分类id
     * @param pageNum       第几页
     * @param pageSize      每个显示多少条
     * @param orderBy       以什么字段排排序
     * @return
     */
    @Override
    public ServerResponse<PageInfo> getProductByKeyworkCategory(String keywork,Integer categoryId, Integer pageNum, Integer pageSize, String orderBy){
        //如果关键字和分类id都为空，返回一个参数错误
        if(StringUtils.isBlank(keywork) && categoryId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        if(StringUtils.isNotBlank(keywork)){
            keywork = new StringBuffer().append("%").append(keywork).append("%").toString();
        }
        //用于存放该分类的所有子分类id
        List<Integer> categoryIdList = Lists.newArrayList();
        if(categoryId != null){
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            //如果没有该分类，并且关键字为空，返回一个空的list
            if(category == null && StringUtils.isBlank(keywork)){
                PageHelper.startPage(pageNum,pageSize);
                List<ProductListVo> list = Lists.newArrayList();
                PageInfo pageInfo = new PageInfo(list);
                return ServerResponse.createBySuccess(pageInfo);
            }
            //查询该分类的所有子分类id
            categoryIdList = iCategoryService.selectCategoryAndChildrenById(categoryId).getData();
        }

        PageHelper.startPage(pageNum,pageSize);
        //排序处理
        if(StringUtils.isNotBlank(orderBy)){
            if(Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)){
                String[] orderBys = orderBy.split("_");
                PageHelper.orderBy(orderBys[0] + " " + orderBys[1]);
            }
        }
        //这里对参数进行了处理，如果是空的内容，直接传null
        List<Product> productList = productMapper.selectByNameAndCategory(StringUtils.isBlank(keywork) ? null : keywork,
                                                categoryIdList.size() == 0 ? null : categoryIdList);
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for (Product product : productList){
            ProductListVo productListVo = assembleProductListVo(product);
            productListVoList.add(productListVo);
        }

        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productListVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }

}

