package com.xsaxc.code.filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import javax.servlet.*;
import java.io.IOException;

/**
 * @author 师晓峰
 * @version V1.0
 * @date 2023/9/28 11:50
 * @Description:
 * Filter 过滤
 *
 */
@Configuration
public class AllFilter implements Filter {



    @Bean
    public FilterRegistrationBean<AllFilter> getAllFilter(){
        FilterRegistrationBean<AllFilter> f = new FilterRegistrationBean<AllFilter>();
        f.setFilter(new AllFilter());
        f.addUrlPatterns("/*");
        return f;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        filterChain.doFilter(servletRequest,servletResponse);
    }

    @Override
    public void destroy() {

    }
}
