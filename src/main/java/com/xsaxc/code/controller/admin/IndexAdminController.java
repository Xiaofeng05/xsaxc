package com.xsaxc.code.controller.admin;

import com.xsaxc.code.service.ArticleService;
import com.xsaxc.code.service.UserService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author 师晓峰
 * @version V1.0
 * @date 2023/10/3 21:03
 * @Description:
 *      管理员的首页
 *          首页/url跳转
 */
@Controller
@RequestMapping("/admin")
public class IndexAdminController {


    @Autowired
    private UserService userService;

    @Autowired
    private ArticleService articleService;

    /**
     * 跳转到管理员主页面
     */
    @RequiresPermissions(value="进入管理员主页")
    @RequestMapping("/toAdminUserCenterPage")
    public String toAdminUserCenterPage(){
        return "admin/index";
    }


    /**
     * 跳转到管理员主页面
     */
    @RequiresPermissions(value="进入管理员主页")
    @RequestMapping("/defaultIndex")
    public ModelAndView defaultIndex(){
        ModelAndView mav = new ModelAndView();
        Long userNum = userService.getCount(null, null, null);
        mav.addObject("userNum",userNum);
        mav.addObject("todayRegister",userService.todayRegister());
        mav.addObject("todayLogin",userService.todayLogin());
        mav.addObject("todayPublish",articleService.todayPublish());
        mav.addObject("noAudit",articleService.noAudit());
        mav.setViewName("admin/default");
        return  mav;
    }

}



