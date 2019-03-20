package com.mmall.dao;

import com.mmall.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    /**
     * 查找用户名是否存在
     * @param username
     * @return
     */
    int checkUserName(String username);

    /**
     * 查找箱是否存在
     * @param email
     * @return
     */
    int checkEmail(String email);

    /**
     * 校对用户名密码是否匹配
     * @param username
     * @param password
     * @return
     */
    User selectLogin(@Param("username")String username, @Param("password") String password);

    /**
     * 根据用户名查找问题
     * @param username
     * @return
     */
    String selectQuestionByUsername(String username);

    /**
     * 校验用户的回答是否正确
     * @param username
     * @param question
     * @param answer
     * @return
     */
    int checkAnswer(@Param("username")String username,@Param("question")String question,@Param("answer")String answer);

    /**
     * 更新密码
     * @param username
     * @param passwordNew
     * @return
     */
    int updatePasswordByUsername(@Param("username")String username,@Param("passwordNew")String passwordNew);

    /**
     * 验证密码与id是否匹配
     * @param passwordOld
     * @param userId
     * @return
     */
    int checkPassord(@Param("passwordOld")String passwordOld,@Param("userId")int userId);

    /**
     * 查询email是否已被占用
     * @param email
     * @param userId
     * @return
     */
    int checkEmailByUserId(@Param("email")String email,@Param("userId")Integer userId);

    /**
     * 根据userId查询用户信息
     * @param userId
     * @return
     */
    User selectInformationById(int userId);
}