package com.xsaxc.code.shiro.realm;

import com.xsaxc.code.entity.User;
import com.xsaxc.code.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;

/**
 * @author 师晓峰
 * @version V1.0
 * @date 2023/9/29 17:34
 * @Description: 自定义realm
 */
public class MyRealm extends AuthorizingRealm {

    @Autowired
    private UserService userService;

    /**
     * 授权
     *
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {

        String userName = (String) SecurityUtils.getSubject().getPrincipal();
        User user = userService.findByUserName(userName);
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        Set<String> roles = new HashSet<>();
        if ("管理员".equals(user.getRoleName())) {
            roles.add("管理员");
            info.addStringPermission("进入管理员主页");
            info.addStringPermission("根据id查询资源类型实体");
            info.addStringPermission("添加或者修改资源类型信息");
            info.addStringPermission("删除资源类型信息");


            info.addStringPermission("分页查询新资源信息");
            info.addStringPermission("删除资源信息");
            info.addStringPermission("根据id获取资源信息");
            info.addStringPermission("审核资源信息");
            info.addStringPermission("生成所有资源信息索引");

            info.addStringPermission("修改热门资源状态");
            info.addStringPermission("修改免费资源状态");

            info.addStringPermission("分页查询评论信息");
            info.addStringPermission("修改该评论状态");
            info.addStringPermission("删除评论信息");


            info.addStringPermission("分页查询用户信息");
            info.addStringPermission("修改用户VIP状态");
            info.addStringPermission("修改用户封禁状态");
            info.addStringPermission("重置用户密码");
            info.addStringPermission("修改用户VIP等级");
            info.addStringPermission("用户充值积分");

            info.addStringPermission("管理员修改密码");
            info.addStringPermission("管理员安全退出");




            info.addStringPermission("分页查询友情链接信息");
            info.addStringPermission("根据id查询友情链接信息");
            info.addStringPermission("批量删除友情链接信息");
            info.addStringPermission("添加或者修改友情链接信息");




        }
        info.setRoles(roles);
        return info;
    }

    /**
     * 鉴权
     *
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        /**
         * 拿到用户名
         */
        String userName = (String) authenticationToken.getPrincipal();
        User user = userService.findByUserName(userName);
        if (user == null) {
            return null;
        } else {
            AuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(user.getUserName(), user.getPassword(), "欢迎登录");
            return authenticationInfo;
        }
    }
}
