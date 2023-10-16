package com.xsaxc.code.controller;
import java.io.*;
import java.net.URL;
import java.util.Date;

import com.qq.connect.QQConnectException;
import com.qq.connect.api.OpenID;
import com.qq.connect.api.qzone.UserInfo;
import com.qq.connect.javabeans.AccessToken;
import com.qq.connect.javabeans.qzone.UserInfoBean;
import com.qq.connect.oauth.Oauth;
import com.xsaxc.code.entity.ArcType;
import com.xsaxc.code.entity.Article;
import com.xsaxc.code.entity.User;
import com.xsaxc.code.service.ArcTypeService;
import com.xsaxc.code.service.ArticleService;
import com.xsaxc.code.service.MessageService;
import com.xsaxc.code.service.UserService;
import com.xsaxc.code.util.*;
import org.apache.commons.io.FileUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * @author 师晓峰
 * @version V1.0
 * @date 2023/9/28 2:50
 * @Description:
 *      根路径及其他请求处理
 */
@Controller
//@RequestMapping("/front")
public class IndexController {

    @Autowired
    private ArcTypeService arcTypeService;

    @Autowired
    private ArticleService articleService;


    @Autowired
    private MessageService messageService;


    @Autowired
    private UserService userService;

    /**
     * 图片上传路径
     */
    @Value("${imgFilePath}")
    private String imgFilePath;

    @RequestMapping("/")
    public ModelAndView index(){
        ModelAndView mav = new ModelAndView();
        mav.setViewName("index");
        List<ArcType> arcTypeList = arcTypeService.list(Sort.Direction.ASC,"sort");

        // 类型的HTML代码
        mav.addObject("actTypeStr", HTMLUtil.getArcTypeStr("all",arcTypeList));

        // 资源列表
        Map<String, Object> map = articleService.list("all", 1, Const.PAGE_SIZE);
        mav.addObject("articleList",map.get("data"));
        // 分页HTML代码
        mav.addObject("pageStr",HTMLUtil.getLayPage("http://120.46.82.82:8080/code/article/all",Integer.parseInt(String.valueOf(map.get("count"))),1,"该分类还没有数据..."));

        return mav;
    }

    /**
     * QQ 回调
     */
    @RequestMapping("/content")
    public String qqCallBack(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws QQConnectException, IOException {
        response.setContentType("text/html;charset=utf-8");
        AccessToken accessTokenObj = new Oauth().getAccessTokenByRequest(request);
        String accessToken = null;
        String openId = null;
        String state = request.getParameter("state");
        String session_state = (String) session.getAttribute("qq_content_state");
        if (StringUtil.isEmpty(session_state) || !session_state.equals(state)) {
            System.out.println("非法请求");
            return "redirect:/";
        }
        accessToken = accessTokenObj.getAccessToken();
        if(StringUtil.isEmpty(accessToken)){
            System.out.println("没有获取到响应参数");
            return "redirect:/";
        }
        session.setAttribute("accessToken", accessToken);
        OpenID openIDObj = new OpenID(accessToken);

        openId = openIDObj.getUserOpenID();
        UserInfo qzoneInfo = new UserInfo(accessToken,openId);
        UserInfoBean userInfoBean = qzoneInfo.getUserInfo();
        if (userInfoBean == null || userInfoBean.getRet() != 0 || StringUtil.isNotEmpty(userInfoBean.getMsg())){
            System.out.println("没有对应的QQ信息");
            return "redirect:/";
        }
        // 获取用户成功
        User user = userService.getUserByOpenId(openId);
        if (user == null){
            // 用户第一次登录
            user = new User();

            user.setOpenId(openId);
            user.setNickname(userInfoBean.getNickname());
            String imgPath = DateUtil.getCurrentDate()+".jpg";
            downloadPicture(userInfoBean.getAvatar().getAvatarURL100(),imgFilePath+"/"+imgPath);

            user.setHeadPortrait(imgPath);
            user.setSex(userInfoBean.getGender());
            user.setPassword(CryptographyUtil.md5("123123",CryptographyUtil.SALT));
            user.setRegisterDate(new Date());
            user.setLastLoginTime(new Date());
//            userService.save(user);
            session.setAttribute(Const.CURRENT_USER,user);
        }else {
            // 注册过  更新用户信息 直接将信息放入session中
            if (!user.getOff()){
                user.setNickname(userInfoBean.getNickname());
                user.setSex(userInfoBean.getGender());
                user.setLastLoginTime(new Date());
                Subject subject = SecurityUtils.getSubject();
                UsernamePasswordToken token = new UsernamePasswordToken(user.getUserName(),user.getPassword());
                subject.login(token);

                // 未读消息 放到session中
                Integer messageCount = messageService.getCountByUserId(user.getUserId());
                user.setMessageCount(messageCount);

                userService.save(user);

                // 失效资源数
                Article s_article =  new Article();
                s_article.setUseful(false);
                s_article.setUser(user);
                long unUsefulArticleLinkCount =  articleService.getCount(s_article,null,null,null);
                session.setAttribute(Const.UN_USEFUL_ARTICLE_LINK_COUNT,unUsefulArticleLinkCount);

                session.setAttribute(Const.CURRENT_USER,user);
            }
        }

        return "redirect:/";
    }


    /**
     * 通过链接下载图片保存头像文件夹
     * @param urlString
     * @param path
     */
    public void downloadPicture(String urlString,String path) throws IOException {
        URL url =  null;
        DataInputStream dataInputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
             url = new URL(urlString);
             dataInputStream = new DataInputStream(url.openStream());
             fileOutputStream = new FileOutputStream(new File(path));
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];

            int len = 0;

            while((len = dataInputStream.read(buffer))>0){
                output.write(buffer, 0, len);
            }

            fileOutputStream.write(output.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            dataInputStream.close();
            fileOutputStream.close();
        }
    }

    /**
     * 购买vip
     * @return
     */
    @RequestMapping("/buyVIP")
    public String bugVIP(){
        return "buyVIP";
    }

    /**
     * 发布资源赚积分
     * @return
     */
    @RequestMapping("/fbzyzjf")
    public String fbzyzjf(){
        return "fbzyzjf";
    }
}
