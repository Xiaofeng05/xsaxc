package com.xsaxc.code.controller;

import com.qq.connect.QQConnectException;
import com.qq.connect.oauth.Oauth;
import com.xsaxc.code.service.ArticleService;
import com.xsaxc.code.service.MessageService;
import com.xsaxc.code.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author 师晓峰
 * @version V1.0
 * @date 2023/10/15 19:55
 * @Description:
 *      QQ登录请求
 */
@Controller
@RequestMapping("/QQ")
public class QQController {

    @Autowired
    private UserService userService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private ArticleService articleService;

    /**
     * 图片上传路径
     */
    @Value("${imgFilePath}")
    private String imgFilePath;


    @RequestMapping("/qqlogin")
    public void qqLogin(HttpServletRequest request, HttpServletResponse response){
        response.setContentType("text/html;charset=utf-8");
        try {
            response.sendRedirect(new Oauth().getAuthorizeURL(request));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (QQConnectException e) {
            e.printStackTrace();
            new QQConnectException("跳转到QQ登录页面异常");
        }
    }



}
