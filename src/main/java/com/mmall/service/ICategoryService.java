package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;

import java.util.List;

/**
 * Created by Administrator on 2019/3/12/012.
 */
public interface ICategoryService {

    ServerResponse addCategory(String categroyName, Integer parentId);

    ServerResponse setCategoryName(String categoryName, Integer categoryId);

    ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId);

    ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId);
}
