package com.xsaxc.code.respository;

import com.xsaxc.code.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

/**
 * @author 师晓峰
 * @version V1.0
 * @date 2023/9/29 16:58
 * @Description:
 *      用户Repository接口
 */

public interface UserRepository extends JpaRepository<User,Integer>, JpaSpecificationExecutor<User> {


    /**
     * 根据用户名查找用户实体
     * @param userName
     * @return
     */
    @Query(value = "select * from user where user_name =?1",nativeQuery = true)
    public User findByUserName(String userName);


    /**
     * 根据邮箱查找用户实体
     * @param email
     * @return
     */
    @Query(value = "select * from user where email =?1",nativeQuery = true)
    public User findByEmail(String email);

    /**
     * 今日注册的人数
     * @return
     */
    @Query(value = "select count(*) from user where TO_DAYS(register_date) = TO_DAYS(NOW())",nativeQuery = true)
    public Integer todayRegister();

    /**
     * 今日登录用户的人数
     * @return
     */
    @Query(value = "select count(*) from user where TO_DAYS(last_login_time) = TO_DAYS(NOW())",nativeQuery = true)
    public Integer todayLogin();


    /**
     * 根据openId 获取用户信息
     * @param openId
     * @return
     */
    @Query(value = "select * from user where open_id = ?1 limit 1",nativeQuery = true)
    public User getUserByOpenId(String openId);
}


