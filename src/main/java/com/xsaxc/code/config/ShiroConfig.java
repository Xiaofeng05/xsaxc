package com.xsaxc.code.config;


import com.xsaxc.code.shiro.realm.MyRealm;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 师晓峰
 * @version V1.0
 * @date 2023/9/29 17:46
 * @Description: Shiro的配置类
 * <p>
 * FilterChain定义说明
 * 1.一个URL可以配置多个Filter，使用逗号分割
 * 2.当设置多个过滤器时，全部认证通过，才算通过
 * 3.部分过滤器可以指定参数的，例如perms，roles
 */
@Configuration
public class ShiroConfig {

    @Bean
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        // 必须设置
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        // 如果这里不设置，会默认寻找web工程目录下的login.jsp页面
        shiroFilterFactoryBean.setLoginUrl("/");

        // 设置拦截器
        Map<String, String> filterChainDefinitionMap = new HashMap<String, String>();
        // 过滤链的定义 从上往下执行 一般将/**放在最下面 要小心
        // authc: 所有的URL 必须认证通过才能访问
        // anon： 所有的URL 都可以访问
        // 先配置不会被拦截的
        filterChainDefinitionMap.put("/", "anon");

        filterChainDefinitionMap.put("/static/**", "anon");
        filterChainDefinitionMap.put("/ueditor/**", "anon");
        filterChainDefinitionMap.put("/upload/**", "anon");

        filterChainDefinitionMap.put("/fbzyzjf", "anon");
        filterChainDefinitionMap.put("/buyVIP", "anon");

        filterChainDefinitionMap.put("/user/register.html", "anon");
        filterChainDefinitionMap.put("/user/login.html", "anon");
        filterChainDefinitionMap.put("/user/findPassword.html", "anon");
        filterChainDefinitionMap.put("/user/bindEmail.html", "anon");
        filterChainDefinitionMap.put("/fbzyzjf.html", "anon");
        filterChainDefinitionMap.put("/buyVIP.html", "anon");

        filterChainDefinitionMap.put("/user/register", "anon");
        filterChainDefinitionMap.put("/user/login", "anon");
        filterChainDefinitionMap.put("/QQ/qqlogin", "anon");
        filterChainDefinitionMap.put("/fbzyzjf", "anon");
        filterChainDefinitionMap.put("/buyVIP", "anon");

        filterChainDefinitionMap.put("/user/sendEmail", "anon");
        filterChainDefinitionMap.put("/user/checkCode", "anon");
        filterChainDefinitionMap.put("/user/bindEmail", "anon");


        filterChainDefinitionMap.put("/QQ/qqlogin.html", "anon");
        filterChainDefinitionMap.put("/article/**", "anon");
        filterChainDefinitionMap.put("/comment/**", "anon");
        filterChainDefinitionMap.put("/content/**", "anon");

        filterChainDefinitionMap.put("/admin/login.html", "anon");
        filterChainDefinitionMap.put("/admin/toAdminUserCenterPage", "anon");

        // 配置退出的过滤器 其中具体的代码shiro已经帮助我们实现了。

        filterChainDefinitionMap.put("/user/logout", "logout");

        // 目前先关了吧
        filterChainDefinitionMap.put("/code/**", "authc");

        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;


    }

    @Bean
    public SecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        // 设置realm
        securityManager.setRealm(myRealm());
        return securityManager;
    }

    /**
     * 身份认证的realm
     *
     * @return
     */
    @Bean
    public MyRealm myRealm() {
        return new MyRealm();
    }

    /**
     * shiro 生命周期处理器
     *
     * @return
     */
    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    /**
     * 开启shiro注解（如@ RequiredRoles,@RequiresPermissions)
     * 需要借助SpringAOP扫描使用Shiro的注解类，并在必要的时候进行安全逻辑验证
     * 配置一下两个bean 就可以实现
     *
     * @return
     */

    @Bean
    @DependsOn("lifecycleBeanPostProcessor")
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        advisorAutoProxyCreator.setProxyTargetClass(true);
        return advisorAutoProxyCreator;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor() {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager());
        return authorizationAttributeSourceAdvisor;
    }


}
