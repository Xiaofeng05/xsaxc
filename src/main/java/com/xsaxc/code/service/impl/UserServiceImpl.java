package com.xsaxc.code.service.impl;

import com.xsaxc.code.entity.User;
import com.xsaxc.code.respository.UserRepository;
import com.xsaxc.code.service.UserService;
import com.xsaxc.code.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.List;

/**
 * @author 师晓峰
 * @version V1.0
 * @date 2023/9/29 17:07
 * @Description:
 *      用户Service实现类
 */
@Service(value = "userService")
@Transactional(rollbackOn = Exception.class)
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

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
    @Override
    public List<User> list(User user, String s_blatelyLoginTime, String s_elatelyLoginTime, Integer page, Integer pageSize, Sort.Direction direction, String... properties) {
        Page<User> userPage = userRepository.findAll(new Specification<User>() {
            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate predicate = criteriaBuilder.conjunction();
                if (StringUtil.isNotEmpty(s_blatelyLoginTime)){
                    predicate.getExpressions().add(criteriaBuilder.greaterThanOrEqualTo(root.get("latelyLoginTime").as(String.class),s_blatelyLoginTime));

                }
                if (StringUtil.isNotEmpty(s_elatelyLoginTime)){
                    predicate.getExpressions().add(criteriaBuilder.lessThanOrEqualTo(root.get("latelyLoginTime").as(String.class),s_elatelyLoginTime));

                }
                if (user != null){
                    if (StringUtil.isNotEmpty(user.getSex())){
                        predicate.getExpressions().add(criteriaBuilder.equal(root.get("sex"),user.getSex()));
                    }
                    if (StringUtil.isNotEmpty(user.getUserName())){
                        predicate.getExpressions().add(criteriaBuilder.like(root.get("userName"),"%"+user.getUserName()+"%"));
                    }
                }
                return predicate;
            }
        },PageRequest.of(page - 1, pageSize,direction, properties));
        return userPage.getContent();
    }

    @Override
    public User findByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public User getById(Integer userId) {
        return userRepository.getOne(userId);
    }

    /**
     * 根据条件查询用户总数
     *
     * @param user               用户实体
     * @param s_blatelyLoginTime 最近开始登录时间
     * @param s_elatelyLoginTime 最近结束登录时间
     * @return 总数量
     */
    @Override
    public Long getCount(User user, String s_blatelyLoginTime, String s_elatelyLoginTime) {
        return  userRepository.count(new Specification<User>() {
            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate predicate = criteriaBuilder.conjunction();
                if (StringUtil.isNotEmpty(s_blatelyLoginTime)){
                    predicate.getExpressions().add(criteriaBuilder.greaterThanOrEqualTo(root.get("latelyLoginTime").as(String.class),s_blatelyLoginTime));

                }
                if (StringUtil.isNotEmpty(s_elatelyLoginTime)){
                    predicate.getExpressions().add(criteriaBuilder.lessThanOrEqualTo(root.get("latelyLoginTime").as(String.class),s_elatelyLoginTime));

                }
                if (user != null){
                    if (StringUtil.isNotEmpty(user.getSex())){
                        predicate.getExpressions().add(criteriaBuilder.equal(root.get("sex"),user.getSex()));
                    }
                    if (StringUtil.isNotEmpty(user.getUserName())){
                        predicate.getExpressions().add(criteriaBuilder.like(root.get("userName"),"%"+user.getUserName()+"%"));
                    }
                }

                return predicate;
            }
        });
    }

    /**
     * 今日注册的人数
     *
     * @return Integer
     */
    @Override
    public Integer todayRegister() {
        return userRepository.todayRegister();
    }

    /**
     * 今日登录用户的人数
     *
     * @return Integer
     */
    @Override
    public Integer todayLogin() {
        return userRepository.todayLogin();
    }

    @Override
    public User getUserByOpenId(String openId) {
        return userRepository.getUserByOpenId(openId);
    }
}
