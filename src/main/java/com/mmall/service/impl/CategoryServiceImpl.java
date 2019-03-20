package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2019/3/12/012.
 */
@Service("iCategoryManagerService")
public class CategoryServiceImpl implements ICategoryService {

    Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private IUserService iUserService;

    /**
     * 插入新的商品种类
     * @param categroyName
     * @param parentId
     * @return
     */
    @Override
    public ServerResponse addCategory(String categoryName, Integer parentId){
        if(StringUtils.isBlank(categoryName) || parentId == null){
            return ServerResponse.createByErrorMessage("添加商品种类参数错误");
        }

        Category category = new Category();
        category.setParentId(parentId);
        category.setName(categoryName);
        category.setStatus(true);

        int rowCount = categoryMapper.insert(category);
        if(rowCount > 0){
            return ServerResponse.createBySuccessMessage("插入商品种类成功");
        }
        return ServerResponse.createByErrorMessage("插入商品种类失败");
    }

    /**
     *修改品类名
     * @param categoryName
     * @param categoryId
     * @return
     */
    @Override
    public ServerResponse setCategoryName(String categoryName, Integer categoryId){
        if(StringUtils.isBlank(categoryName) || categoryId == null){
            return ServerResponse.createByErrorMessage("修改品类参数错误");
        }

        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);

        int rowCount = categoryMapper.updateByPrimaryKeySelective(category);
        if(rowCount > 0){
            return ServerResponse.createBySuccessMessage("更新品类名成功");
        }
        return ServerResponse.createBySuccessMessage("更新品类名失败");
    }

    /**
     * 查找当前分类的子分类，不递归
     * @param categoryId
     * @return
     */
    @Override
    public ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId){
        if(categoryId == null){
            return ServerResponse.createByErrorMessage("查询品类名参数错误");
        }
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        if(CollectionUtils.isEmpty(categoryList)){
            logger.info("未找到当前分类的子分类");
        }
        return ServerResponse.createBySuccess(categoryList);
    }

    /**
     * 查找当前分类的所有子分类
     * @param categoryId
     * @return
     */
    @Override
    public ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId){
        Set<Category> categorySet = Sets.newHashSet();
        categorySet = findCategoryChildren(categorySet, categoryId);

        List<Integer> idList = Lists.newArrayList();

        for(Category categoryItem : categorySet){
            idList.add(categoryItem.getId());
        }
        return ServerResponse.createBySuccess(idList);
    }

    //递归查所有子节点
    public Set<Category> findCategoryChildren(Set<Category> categorySet,Integer categoryId){
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        categorySet.add(category);

        List<Category> list = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        for(Category categoryItem : list){
            findCategoryChildren(categorySet,categoryItem.getId());
        }
        return categorySet;
    }


}
