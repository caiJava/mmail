package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * Created by Administrator on 2019/3/12/012.
 */
@Controller
@RequestMapping("/manager/category/")
public class CategoryManagerController {

    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private ICategoryService iCategoryService;
    @Autowired
    private IUserService iUserService;

    /**
     * 加入新的节点
     * @param session
     * @param categoryName
     * @param parentId
     * @return
     */
    @RequestMapping(value = "add_category.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse addCategory(HttpSession session, String categoryName, @RequestParam (value = "parentId",defaultValue = "0") Integer parentId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录");
        }
        //判断是不是管理员
        if(iUserService.checkIsAdmin(user).isSuccess()){
            //插入新的品类
            return iCategoryService.addCategory(categoryName, parentId);
        }else{
            return ServerResponse.createByErrorMessage("不是管理员，无限操作");
        }

    }

    /**
     * 修改品类名
     * @param session
     * @param categoryName
     * @param categoryId
     * @return
     */
    @RequestMapping(value = "set_category_name.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse setCategoryName(HttpSession session, String categoryName, Integer categoryId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录");
        }
        //判断是不是管理员
        if(iUserService.checkIsAdmin(user).isSuccess()){
            //更新品类名
            return iCategoryService.setCategoryName(categoryName,categoryId);
        }else{
            return ServerResponse.createByErrorMessage("不是管理员，无权限操作");
        }
    }

    /**
     * 获得子类，不递归
     * @param session
     * @param categoryId
     * @return
     */
    @RequestMapping(value = "get_children_parallel_category.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<List<Category>> getChildrenParallelCategory(HttpSession session,@RequestParam(value = "categoryId", defaultValue = "0")Integer categoryId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录");
        }
        //判断是不是管理员
        if(iUserService.checkIsAdmin(user).isSuccess()){
            //查询子节点的category信息，不递归
            return iCategoryService.getChildrenParallelCategory(categoryId);
        }else{
            return ServerResponse.createByErrorMessage("不是管理员，无权限操作");
        }
    }

    /**
     * 获得所有子类，递归
     * @param session
     * @param categoryId
     * @return
     */
    @RequestMapping(value = "get_deep_category.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<List<Integer>> getCategoryAndDeepChildrenCategory(HttpSession session,@RequestParam(value = "categoryId", defaultValue = "0")Integer categoryId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录");
        }
        //判断是不是管理员
        if(iUserService.checkIsAdmin(user).isSuccess()){
            //查询子节点的子节点的categoryId
            return iCategoryService.selectCategoryAndChildrenById(categoryId);
        }else{
            return ServerResponse.createByErrorMessage("不是管理员，无权限操作");
        }
    }




}
