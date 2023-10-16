package com.xsaxc.code.controller;

import com.xsaxc.code.entity.Article;
import com.xsaxc.code.entity.Message;
import com.xsaxc.code.entity.User;
import com.xsaxc.code.entity.UserDownload;
import com.xsaxc.code.lucene.ArticleIndex;
import com.xsaxc.code.service.*;
import com.xsaxc.code.util.*;
import org.apache.commons.io.FileUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.*;


/**
 * @author 师晓峰
 * @version V1.0
 * @date 2023/9/28 18:37
 * @Description: 用户控制器
 */
@Controller
@RequestMapping("/user")
public class UserController {

    /**
     * 图片的上传路径
     */
    @Value("${imgFilePath}")
    private String imgFilePath;

    @Autowired
    private UserService userService;

    @Resource
    private JavaMailSender mailSender;


    @Autowired
    private ArticleIndex articleIndex;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private UserDownloadService userDownloadService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private MessageService messageService;




    @PostMapping("/register")
    @ResponseBody
    /**
     * 用户注册
     * @param   user
     */
    public Map<String, Object> register(@Valid User user, BindingResult bindingResult) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (bindingResult.hasErrors()) {
            map.put("success", false);
            map.put("errorInfo", bindingResult.getFieldError().getDefaultMessage());
        } else if (userService.findByEmail(user.getEmail()) != null) {
            map.put("success", false);
            map.put("errorInfo", "邮箱已存在，请更改");
        } else if (userService.findByUserName(user.getUserName()) != null) {
            map.put("success", false);
            map.put("errorInfo", "用户已存在，请更改");
        } else {
            // 密码加密
            user.setPassword(CryptographyUtil.md5(user.getPassword(), CryptographyUtil.SALT));
            user.setRegisterDate(new Date());
            user.setLastLoginTime(new Date());
            user.setHeadPortrait("tou.jpg");
            userService.save(user);
            map.put("success", true);
        }
        return map;
    }


    @PostMapping("/login")
    @ResponseBody
    /**
     * 用户注册
     * @param   user
     */
    public Map<String, Object> login(User user, HttpSession session) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (StringUtil.isEmpty(user.getUserName())) {
            map.put("success", false);
            map.put("errorInfo", "请输入用户名！");
        } else if (StringUtil.isEmpty(user.getPassword())) {
            map.put("success", false);
            map.put("errorInfo", "请输入密码！");
        } else {
            Subject subject = SecurityUtils.getSubject();
            UsernamePasswordToken token = new UsernamePasswordToken(user.getUserName(), CryptographyUtil.md5(user.getPassword(), CryptographyUtil.SALT));
            try {
                /**
                 * 登录验证
                 */
                subject.login(token);
                String userName = (String) SecurityUtils.getSubject().getPrincipal();
                User currentUser = userService.findByUserName(userName);
                if (currentUser.getOff()) {
                    map.put("success", false);
                    map.put("errorInfo", "该用户已经被封禁！");
                    subject.logout();
                } else {
                    currentUser.setLastLoginTime(new Date());

                    // 未读消息 放到session中
                    Integer messageCount = messageService.getCountByUserId(currentUser.getUserId());
                    currentUser.setMessageCount(messageCount);

                    userService.save(currentUser);

                    // 失效资源数
                    Article s_article =  new Article();
                    s_article.setUseful(false);
                    s_article.setUser(currentUser);
                    long unUsefulArticleLinkCount =  articleService.getCount(s_article,null,null,null);

                    session.setAttribute(Const.UN_USEFUL_ARTICLE_LINK_COUNT,unUsefulArticleLinkCount);
                    session.setAttribute(Const.CURRENT_USER, currentUser);
                    map.put("success", true);
                }
            } catch (AuthenticationException e) {
                e.printStackTrace();
                map.put("success", false);
                map.put("errorInfo", "用户名或者密码错误");
            }
        }
        return map;
    }


    @PostMapping("/sendEmail")
    @ResponseBody
    /**
     * 发送邮箱
     * @param   user
     */
    public Map<String, Object> sendEmail(String email, HttpSession session) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (StringUtil.isEmpty(email)) {
            map.put("success", false);
            map.put("errorInfo", "邮箱不能为空");
            return map;
        }
        // 验证邮件是否存在
        User user = userService.findByEmail(email);
        if (user == null) {
            map.put("success", false);
            map.put("errorInfo", "邮箱不存在");
            return map;
        }
        String emailCode = StringUtil.genSixRandom();
        // 发送邮件
        // 消息构造器
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        // 发件人
        mailMessage.setFrom("1690514096@qq.com");
        // 收件人
        mailMessage.setTo(email);
        // 主题
        mailMessage.setSubject("小师爱学习-用户找回密码");
        // 正文
        mailMessage.setText("欢迎您加入小师爱学习，你本次的邮箱验证码为：" + emailCode + "，10分钟内有效。");
        mailSender.send(mailMessage);
        System.out.println(emailCode);
        // 将验证码 存入session中
        session.setAttribute(Const.EMAIL_CODE_NAME, emailCode);
        session.setAttribute(Const.USER_ID_NAME, user.getUserId());
        map.put("success", true);

        return map;
    }


    @PostMapping("/checkCode")
    @ResponseBody
    /**
     * 发送邮箱
     * @param   user
     */
    public Map<String, Object> checkCode(String emailCode, HttpSession session) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (StringUtil.isEmpty(emailCode)) {
            map.put("success", false);
            map.put("errorInfo", "验证码不能为空");
            return map;
        }
        String code = (String) session.getAttribute(Const.EMAIL_CODE_NAME);
        Integer userId = (Integer) session.getAttribute(Const.USER_ID_NAME);
        if (!code.equals(emailCode)) {
            map.put("success", false);
            map.put("errorInfo", "验证码错误");
            return map;
        }

        // 给用户重置密码为123123
        User user = userService.getById(userId);
        user.setPassword(CryptographyUtil.md5(Const.DEFAULT_PASSWORD, CryptographyUtil.SALT));
        userService.save(user);
        map.put("success", true);
        return map;
    }


    /**
     * 用户资源管理
     *
     */
    @GetMapping("/articleManage")
    public String articleManage() {
        return "user/articleManage";
    }


    /**
     * 根据条件查询分页资源信息列表，（只显示当前登录的用户的所有资源）
     *
     * @param s_article 资源信息
     * @param page      当前页面
     * @param pageSize  页面记录数量
     * @param session   session 用户信息
     * @return
     */
    @RequestMapping("/articleList")
    @ResponseBody
    public Map<String, Object> articleList(Article s_article, @RequestParam(value = "page", required = false) Integer page,
                                           @RequestParam(value = "limit", required = false) Integer pageSize, HttpSession session) {
        Map<String, Object> map = new HashMap<String, Object>();
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        s_article.setUser(currentUser);
        List<Article> articleList = articleService.list(s_article, null, null, null, page, pageSize, Direction.DESC, "publishDate");

        map.put("data", articleList);
        // 总记录数
        map.put("count", articleService.getCount(s_article, null, null, null));
        map.put("code", 0);
        return map;
    }

    /**
     * 跳转添加资源页面
     *
     * @return
     */
    @GetMapping("/toAddArticle")
    public String toAddArticle() {
        return "user/addArticle";
    }


    /**
     * 资源保存
     */
    @PostMapping("/saveArticle")
    @ResponseBody
    public Map<String, Object> saveArticle(Article s_article, HttpSession session) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();

        /**
         * 积分异常
         */
        if (s_article.getPoints() < 0 || s_article.getPoints() > 10) {
            map.put("success", false);
            map.put("errorInfo", "积分不在正常范围，请重新设置");
            return map;
        }

        /**
         * 百度云链接异常
         */
        if (!CheckShareLinkEnableUtil.check(s_article.getDownload())) {
            map.put("success", false);
            map.put("errorInfo", "链接已失效了，请重新设置");
            return map;
        }

        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);

        if (s_article.getArticleId() == null) {
            // 新增
            s_article.setPublishDate(new Date());
            // 设置当前用户
            s_article.setUser(currentUser);
            // 设置是否免费
            if (s_article.getPoints() == 0) {
                s_article.setFree(true);
            }
            // 设置未审核状态
            s_article.setState(1);
            // 设置一个假数据 (点击数)
            s_article.setClick(new Random().nextInt(150) + 50);
            articleService.save(s_article);
            map.put("success", true);

        } else {
            // 更新
            Article oldArticle = articleService.getById(s_article.getArticleId());

            if (oldArticle.getUser().getUserId().intValue() == currentUser.getUserId().intValue()) {
                // 只能修改自己的资源
                oldArticle.setName(s_article.getName());
                oldArticle.setArcType(s_article.getArcType());
                oldArticle.setContent(s_article.getContent());
                oldArticle.setDownload(s_article.getDownload());
                oldArticle.setPassword(s_article.getPassword());
                oldArticle.setKeywords(s_article.getKeywords());
                oldArticle.setDescription(s_article.getDescription());
                if (oldArticle.getState() == 3) {
                    // 假如原来审核未通过
                    // 更改后变成未审核状态
                    oldArticle.setState(1);
                }
            }
            articleService.save(oldArticle);

            if (oldArticle.getState() == 2) {
                // 更新索引
                articleIndex.updateIndex(oldArticle);
            }
            map.put("success", true);
        }
        return map;
    }

    /**
     * 验证资源发布者信息
     *
     * @param articleId
     * @param session
     * @return
     */
    @RequestMapping("/checkArticleUser")
    @ResponseBody
    public Map<String, Object> checkArticleUser(Integer articleId, HttpSession session) {
        Map<String, Object> map = new HashMap<String, Object>();
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        Article article = articleService.getById(articleId);
        if (article.getUser().getUserId().intValue() == currentUser.getUserId().intValue()) {
            map.put("success", true);
            return map;
        }
        map.put("success", false);
        map.put("errorInfo", "您不是资源所有者，您不可以操作！");
        return map;
    }

    /**
     * 进入资源编辑页面
     *
     * @param articleId
     * @return
     */
    @RequestMapping("/toEditArticle/{articleId}")
    public ModelAndView toEditArticle(@PathVariable(value = "articleId", required = true) Integer articleId) {
        ModelAndView mav = new ModelAndView();
        Article article = articleService.getById(articleId);
        mav.addObject("article", article);
        mav.setViewName("user/editArticle");
        return mav;
    }


    /**
     * 根据资源id 删除资源
     *
     * @param articleId
     * @param session
     * @return
     */
    @PostMapping("/articleDelete")
    @ResponseBody
    public Map<String, Object> articleDelete(Integer articleId, HttpSession session) {
        Map<String, Object> map = new HashMap<>();
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        Article article = articleService.getById(articleId);
        if (article.getUser().getUserId().intValue() == currentUser.getUserId().intValue()) {
            // 删除评论信息
            commentService.deleteByArticleId(articleId);
            // 删除用户下载信息关联
            userDownloadService.deleteByArticleId(articleId);
            articleService.deleteById(articleId);
            // 删除资源索引
            articleIndex.deleteIndex(String.valueOf(articleId));
            map.put("success", true);
            return map;
        } else {
            map.put("success", false);
            map.put("errorInfo", "您不是资源所有者，您不可以操作！");
        }
        return map;
    }


    /**
     * 判断用户是否下载过此资源
     *
     * @param articleId 资源的id
     * @param session  存储用户信息
     * @return  用户是否下载过 true 下载过 false 没下载过
     */
    @ResponseBody
    @RequestMapping("/userDownloadExist")
    public boolean userDownloadExist(Integer articleId, HttpSession session) {
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        Integer count = userDownloadService.getCountBuUserIdAndArticleId(currentUser.getUserId(), articleId);
        return count > 0;
    }


    /**
     * 判断用户积分是否足够下载资源
     * @param points 所需积分
     * @param session 存储用户信息
     * @return 是否足够下载资源 true false
     */
    @ResponseBody
    @RequestMapping("/userDownloadEnough")
    public boolean userDownloadEnough(Integer points, HttpSession session) {
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        return currentUser.getPoints() > points;
    }


    /**
     * 普通用户下载
     * @param articleId
     * @param session
     * @return
     */
    @RequestMapping("/toDownloadPage/{articleId}")
    public ModelAndView toDownloadPage(@PathVariable(value = "articleId") Integer articleId, HttpSession session) {
        ModelAndView mav = new ModelAndView();
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (currentUser == null){
            // 用户没登录也直接返回
            return mav;
        }
        Article article = articleService.getById(articleId);
        if (article != null && article.getState() != 2 ){
            // 查不到或者审核不同过 直接返回
            return null;
        }

        Integer count = userDownloadService.getCountBuUserIdAndArticleId(currentUser.getUserId(), articleId);
        // 判断当前用户是否下载过
        if (count == 0){
            // 是否免费
            if (article.isFree()){
                // 积分积分是否足够  扣除积分
                if (userDownloadEnough(article.getPoints(),session)){
                    currentUser.setPoints(currentUser.getPoints() - article.getPoints());
                    userService.save(currentUser);
                    // 资源的分享者需要增加相应积分
                    User articleUser = article.getUser();
                    articleUser.setPoints(articleUser.getPoints() + article.getPoints());
                    userService.save(articleUser);

                    // 用户下载信息保存
                    UserDownload userDownload = new UserDownload();
                    userDownload.setDownloadDate(new Date());
                    userDownload.setUser(currentUser);
                    userDownload.setArticle(article);
                    userDownloadService.save(userDownload);

                }else {
                    //积分积分是否足够
                    return null;
                }
            }
        }else {

        }
        mav.addObject("article",article);
        mav.setViewName("article/downloadPage");

        return mav;
    }


    /**
     * vip 用户下载
     * @param articleId
     * @param session
     * @return
     */
    @RequestMapping("/toVIPDownloadPage/{articleId}")
    public ModelAndView toVIPDownloadPage(@PathVariable(value = "articleId") Integer articleId, HttpSession session) {
        ModelAndView mav = new ModelAndView();
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (currentUser == null || !currentUser.getVip()){
            // 用户没登录也直接返回
            return mav;
        }
        Article article = articleService.getById(articleId);
        if (article != null && article.getState() != 2 ){
            // 查不到或者审核不同过 直接返回
            return null;
        }
        // 判断当前用户是否下载过
        Integer count = userDownloadService.getCountBuUserIdAndArticleId(currentUser.getUserId(), articleId);
        // 判断当前用户是否下载过
        if (count == 0){
            // 用户下载信息保存
            UserDownload userDownload = new UserDownload();
            userDownload.setDownloadDate(new Date());
            userDownload.setUser(currentUser);
            userDownload.setArticle(article);
            userDownloadService.save(userDownload);
        }
        mav.addObject("article",article);
        mav.setViewName("article/downloadPage");

        return mav;
    }



    /**
     * 判断用户是否是vip
     *
     * @return  用户是否下载过 true 下载过 false 没下载过
     */
    @ResponseBody
    @RequestMapping("/isVip")
    public boolean isVip(HttpSession session) {
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        return currentUser.getVip();
    }

    /**
     * 用户失效资源链接数
     * @param session
     */
    private void unUsefulArticleLinkCount(HttpSession session){
        User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
        Article s_article = new Article();
        s_article.setUseful(false);
        s_article.setUser(currentUser);
        session.setAttribute(Const.UN_USEFUL_ARTICLE_LINK_COUNT,articleService.getCount(s_article,null,null,null));
    }

    /**
     * 进入资源失效页面
     * @param session
     * @return
     */
    @RequestMapping("/toUnUsefulArticleManage")
    public String toUnUsefulArticleManager(HttpSession session){
        this.unUsefulArticleLinkCount(session);
        return "user/UnUsefulArticleManager";
    }

    /**
     * 进入我的主页页面
     * @param session
     * @return
     */
    @RequestMapping("/home")
    public ModelAndView home(HttpSession session){
        ModelAndView mav = new ModelAndView();
        User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
        Page<UserDownload> userDownloadPage = userDownloadService.list(currentUser.getUserId(),1,Const.PAGE_SIZE,Direction.DESC,"downloadDate");
        if (userDownloadPage.getTotalElements() > 0){
            // 用户最近下载的资源
            mav.addObject("userDownloadList",userDownloadPage.getContent());
        }
        Page<Message> messagePage= messageService.list(currentUser.getUserId(), 1,Const.PAGE_SIZE,Direction.DESC,"publishDate");
        if (messagePage.getTotalElements() > 0){
            // 用户最近最近的系统消息
            mav.addObject("messageList",messagePage.getContent());
        }
        mav.setViewName("user/home");
        return mav;
    }


    /**
     * 进入已下载资源页面
     * @param session
     * @return
     */
    @RequestMapping("/toHaveDownloaded/{currentPage}")
    public ModelAndView toHaveDownloaded(@PathVariable(value = "currentPage" ,required = false) Integer currentPage, HttpSession session){
        ModelAndView mav = new ModelAndView();
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        // 已下载的资源信息
        Page<UserDownload> userDownloadPage = userDownloadService.list(user.getUserId(), currentPage,Const.PAGE_SIZE,Direction.DESC,"downloadDate");
        mav.addObject("userDownloadList",userDownloadPage.getContent());
        // 分页的html代码
        String targetUrl = "http://120.46.82.82:8080/code/user/toHaveDownloaded";
        int total = userDownloadPage.getTotalPages();
        String msg = "没有下载任何资源...";
        mav.addObject("pageStr", HTMLUtil.getLayPage(targetUrl,total,currentPage,msg));
        mav.setViewName("user/haveDownloaded");

        return mav;
    }


    /**
     * 进入我的消息页面
     * @param session
     * @return
     */
    @RequestMapping("/userManage/{currentPage}")
    @Transactional(rollbackOn = Exception.class)
    public ModelAndView userMessage(@PathVariable(value = "currentPage" ,required = false) Integer currentPage, HttpSession session){
        ModelAndView mav = new ModelAndView();
        User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
        // 进入此页面 用户默认用户查看所有消息
        if (currentUser.getMessageCount() == null||currentUser.getMessageCount() > 0 ){
            messageService.updateStatus(currentUser.getUserId());
            // 用户未读消息数是0
            currentUser.setMessageCount(0);
        }
        // 更新Session用户
        session.setAttribute(Const.CURRENT_USER,currentUser);

        Page<Message> messagePage= messageService.list(currentUser.getUserId(), currentPage,Const.PAGE_SIZE,Direction.DESC,"publishDate");
        mav.addObject("messageList",messagePage.getContent());
        // 分页的html代码
        String targetUrl = "http://120.46.82.82:8080/code/user/userManage";
        int total = messagePage.getTotalPages();
        String msg = "没有下载任何资源...";
        mav.addObject("pageStr", HTMLUtil.getLayPage(targetUrl,total,currentPage,msg));
        mav.setViewName("user/messageManage");

        return mav;
    }


    /**
     * 修改百度云分享链接
     * @param session
     * @return
     */
    @ResponseBody
    @RequestMapping("/modifyArticleShareLink")
    public Map<String,Object> modifyArticleShareLink(Article article,HttpSession session) throws Exception {
        Map<String,Object> map = new HashMap<String,Object>();
        if(CheckShareLinkEnableUtil.check(article.getDownload())){
           Article oldArticle = articleService.getById(article.getArticleId());
           User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
           if (currentUser.getUserId().intValue() == oldArticle.getUser().getUserId().intValue()){
               oldArticle.setDownload(article.getDownload());
               oldArticle.setPassword(article.getPassword());
               oldArticle.setUseful(true);
               articleService.save(oldArticle);
               map.put("success", true);
               this.unUsefulArticleLinkCount(session);
           }else{
               map.put("success", false);
               map.put("errorInfo", "您不是该资源的拥有者！");
           }
        }else{
            map.put("success", false);
            map.put("errorInfo", "您分享的链接已经失效，请重新发布");
        }

        return map;
    }

    @ResponseBody
    @RequestMapping("/updateHeadPortrait")
    public Map<String,Object> updateHeadPortrait(MultipartFile file ,HttpSession session) throws IOException {
        Map<String,Object> map = new HashMap<String,Object>();
        if (!file.isEmpty()){
            // 获取文件名
            String fileName = file.getOriginalFilename();
            // 获取文件的后缀名
            String suffixName = fileName.substring(fileName.lastIndexOf('.'));
            // 新的文件名 (用户数据量小)
            String newFileName = DateUtil.getCurrentDate()+suffixName;
            FileUtils.copyInputStreamToFile(file.getInputStream(),new File(imgFilePath+"/"+newFileName));
            map.put("success",true);
            map.put("imgName",newFileName);
            // 把头像放到session和数据库中
            User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
            currentUser.setHeadPortrait(newFileName);

            userService.save(currentUser);
            session.setAttribute(Const.CURRENT_USER,currentUser);

//            // 从到项目中来 不然后面的(本地
//            FileUtils.copyInputStreamToFile(file.getInputStream(),new File("D:/Springboot_Java/code/src/main/webapp/static/img/"+newFileName));
            // 从到项目中来 不然后面的(本地
            FileUtils.copyInputStreamToFile(file.getInputStream(),new File("/home/code/src/main/webapp/static/img/"+newFileName));

        }
        return map;
    }

    /**
     * 进入用户中心
     * @param session
     * @return
     */
    @GetMapping("/userCenter")
    public ModelAndView userCenter(HttpSession session){
        unUsefulArticleLinkCount(session);
        ModelAndView mav = new ModelAndView();
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        mav.addObject(Const.CURRENT_USER,currentUser);
        mav.setViewName("user/userCenter");
        return mav;
    }

    /**
     * 用户更新
     * @param user
     * @param session
     * @return
     */
    @ResponseBody
    @RequestMapping("/userUpdate")
    public Map<String,Object> userUpdate(User user ,HttpSession session){
        Map<String,Object> map = new HashMap<>();
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        currentUser.setNickname(user.getNickname());
        currentUser.setSex(user.getSex());
        userService.save(currentUser);
        session.setAttribute(Const.CURRENT_USER,currentUser);
        map.put("success", true);
        return map;
    }

    /**
     * 用户修改密码
     * @param oldPassword
     * @param newPassword
     * @param session
     * @return
     */
    @ResponseBody
    @RequestMapping("/updatePassword")
    public Map<String,Object> updatePassword(String oldPassword,String newPassword,HttpSession session){
        Map<String,Object> map = new HashMap<>();
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (currentUser.getPassword().equals(CryptographyUtil.md5(oldPassword,CryptographyUtil.SALT))){
            User oldUser = userService.getById(currentUser.getUserId());
            oldUser.setPassword(CryptographyUtil.md5(newPassword,CryptographyUtil.SALT));
            userService.save(oldUser);
            session.setAttribute(Const.CURRENT_USER,currentUser);
            map.put("success", true);
        }else{
            map.put("success", false);
            map.put("errorInfo", "原密码错误！");
        }
        return map;
    }

    /**
     *
     */
    @ResponseBody
    @RequestMapping("/bindEmail")
    public Map<String,Object> bindEmail(String userName,String email,HttpSession session){
        Map<String,Object> map = new HashMap<String,Object>();
        if (StringUtil.isEmpty(userName)){
            map.put("success",false);
            map.put("errorInfo","用户名为空");
            return  map;
        }
        if (StringUtil.isEmpty(email)){
            map.put("success",false);
            map.put("errorInfo","邮箱为空");
            return  map;
        }
        if(userService.findByUserName(userName) != null){
            map.put("success",false);
            map.put("errorInfo","用户名已经存在，请更换");
            return  map;
        }
        if(userService.findByEmail(email) != null){
            map.put("success",false);
            map.put("errorInfo","邮箱已经存在，请更换");
            return  map;
        }
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (StringUtil.isNotEmpty(currentUser.getUserName()) || StringUtil.isNotEmpty(currentUser.getEmail())){
            map.put("success",false);
            map.put("errorInfo","非法请求");
            return  map;
        }

        currentUser.setUserName(userName);
        currentUser.setEmail(email);


        // 未读消息 放到session中
        Integer messageCount = messageService.getCountByUserId(currentUser.getUserId());
        currentUser.setMessageCount(messageCount);

        userService.save(currentUser);

        // 失效资源数
        Article s_article =  new Article();
        s_article.setUseful(false);
        s_article.setUser(currentUser);
        long unUsefulArticleLinkCount =  articleService.getCount(s_article,null,null,null);
        session.setAttribute(Const.UN_USEFUL_ARTICLE_LINK_COUNT,unUsefulArticleLinkCount);


        session.setAttribute(Const.CURRENT_USER,currentUser);
        map.put("success",true);
        return map;
    }
}
