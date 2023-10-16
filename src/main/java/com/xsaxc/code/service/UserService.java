package com.xsaxc.code.service;

import com.xsaxc.code.entity.User;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


/**
 * @author 师晓峰
 * @version V1.0
 * @date 2023/9/29 17:05
 * @Description: 用户service接口
 */
public interface UserService {


    /**
     * 查询所有用户信息
     *
     * @param user               用户数实体
     * @param s_blatelyLoginTime 登录开始时间
     * @param s_elatelyLoginTime 登录结束时间
     * @param page               当前页数
     * @param pageSize           每页记录数
     * @param direction          排序条件
     * @param properties         参数
     * @return 返回用户列表信息
     */
    public List<User> list(User user, String s_blatelyLoginTime, String s_elatelyLoginTime, Integer page, Integer pageSize, Direction direction, String... properties);

    /**
     * 根据用户名查找用户实体
     *
     * @param userName
     * @return
     */
    public User findByUserName(String userName);


    /**
     * 根据邮箱查找用户实体
     *
     * @param email
     * @return
     */
    public User findByEmail(String email);

    /**
     * 添加和修改用户信息
     *
     * @param user
     * @return
     */
    public void save(User user);

    /**
     * 根据用户id查找用户信息
     *
     * @param userId
     * @return
     */
    public User getById(Integer userId);

    /**
     * 根据条件查询用户总数
     *
     * @param user               用户实体
     * @param s_blatelyLoginTime 最近开始登录时间
     * @param s_elatelyLoginTime 最近结束登录时间
     * @return 总数量
     */
    public Long getCount(User user, String s_blatelyLoginTime, String s_elatelyLoginTime);


    /**
     * 今日注册的人数
     *
     * @return
     */
    public Integer todayRegister();

    /**
     * 今日登录用户的人数
     *
     * @return
     */
    public Integer todayLogin();



    /**
     * 根据openId 获取用户信息
     * @param openId
     * @return
     */
    public User getUserByOpenId(String openId);

}
