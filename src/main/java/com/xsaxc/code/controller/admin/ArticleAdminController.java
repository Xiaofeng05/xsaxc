package com.xsaxc.code.controller.admin;

import com.xsaxc.code.entity.Article;
import com.xsaxc.code.entity.Message;
import com.xsaxc.code.lucene.ArticleIndex;
import com.xsaxc.code.service.ArticleService;
import com.xsaxc.code.service.CommentService;
import com.xsaxc.code.service.MessageService;
import com.xsaxc.code.service.UserDownloadService;
import com.xsaxc.code.util.StringUtil;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 师晓峰
 * @version V1.0
 * @date 2023/10/5 15:34
 * @Description: 管理员 资源管理器
 */
@RestController
@RequestMapping("/admin/article")
@Transactional(rollbackOn = Exception.class)
public class ArticleAdminController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;

    @Autowired
    private ArticleIndex articleIndex;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserDownloadService userDownloadService;

    @Autowired
    private MessageService messageService;


    /**
     * 根据条件分页查询资源信息列表
     *
     * @param s_article    资源信息实体
     * @param nickname     用户昵称
     * @param publishDates 发布时间段
     * @param page         当前第几页
     * @param pageSize     每页的记录数
     * @return
     */
    @RequestMapping("/list")
    @RequiresPermissions("分页查询新资源信息")
    public Map<String, Object> list(Article s_article, @RequestParam(value = "nickname", required = false) String nickname,
                                    @RequestParam(value = "publishDates", required = false) String publishDates,
                                    @RequestParam(value = "page", required = false) Integer page,
                                    @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        Map<String, Object> map = new HashMap<String, Object>();
        String s_bpublishDate = null;
        String s_epublishDate = null;
        if (StringUtil.isNotEmpty(publishDates)) {
            // 拆分时间段
            String[] str = publishDates.split("-");
            s_bpublishDate = str[0];
            s_epublishDate = str[1];
        }
        List<Article> articleList = articleService.list(s_article, nickname, s_bpublishDate, s_epublishDate, page, pageSize, Direction.DESC, "publishDate");

        int count = articleService.getCount(s_article, nickname, s_bpublishDate, s_epublishDate).intValue();
        map.put("data", articleList);
        map.put("total", count);
        map.put("errorNo", 0);
        return map;
    }

    /**
     * 批量删除资源
     *
     * @param ids
     * @return
     */
    @RequestMapping("/delete")
    @RequiresPermissions("删除资源信息")
    public Map<String, Object> delete(@RequestParam(value = "articleId") String ids) {
        Map<String, Object> map = new HashMap<String, Object>();
        String[] idsStr = ids.split(",");
        for (int i = 0; i < idsStr.length; i++) {
            // 删除评论信息
            commentService.deleteByArticleId(Integer.parseInt(idsStr[i]));
            // 删除用户下载信息关联
            userDownloadService.deleteByArticleId(Integer.parseInt(idsStr[i]));
            articleService.deleteById(Integer.parseInt(idsStr[i]));
            // 删除索引
            articleIndex.deleteIndex(idsStr[i]);
        }
        // 处理缓存信息
        map.put("errorNo", 0);
        return map;
    }

    /**
     * 查看或者审核资源信息
     * @param articleId
     * @return
     */
    @RequestMapping("/findById")
    @RequiresPermissions("根据id获取资源信息")
    public Map<String, Object> toModifyArticlePage(Integer articleId) {
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Object> temp = new HashMap<String, Object>();

        Article article = articleService.getById(articleId);
        temp.put("articleId",article.getArticleId());
        temp.put("name",article.getName());
        temp.put("arcType",article.getArcType().getArcTypeId());
        temp.put("points",article.getPoints());
        temp.put("content",article.getContent());
        temp.put("download",article.getDownload());
        temp.put("password",article.getPassword());
        temp.put("click",article.getClick());
        temp.put("keywords",article.getKeywords());
        temp.put("description",article.getDescription());

        map.put("data", temp);
        map.put("errorNo", 0);
        return map;
    }


    /**
     * 审核资源信息
     * @param article
     * @return
     */
    @RequestMapping("/updateState")
    @RequiresPermissions("审核资源信息")
    public Map<String, Object> updateState(Article article) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();

        Article oldArticle = articleService.getById(article.getArticleId());

        Message message = new Message();
        message.setUser(oldArticle.getUser());
        message.setPublishDate(new Date());
        message.setArticleId(oldArticle.getArticleId());
        oldArticle.setCheckDate(new Date());
        if (article.getState() == 2){
            message.setContent("【<font color='green'>审核成功</font>】您发布的<font color='#5FB878'>"+oldArticle.getName()+"</font>资源审核成功！");
            oldArticle.setState(2);

        }else if (article.getState() == 3){
            message.setContent("【<font color='red'>审核失败</font>】您发布的<font color='#5FB878'>"+oldArticle.getName()+"</font>资源审核失败！");
            message.setCause(article.getReason());
            oldArticle.setState(3);
            oldArticle.setReason(article.getReason());
        }

        messageService.save(message);

        articleService.save(oldArticle);
        if (oldArticle.getState() == 2){
            oldArticle.setContentNoTag(StringUtil.stripHtml(oldArticle.getContent()));
            // 添加索引
            articleIndex.addIndex(oldArticle);
            redisTemplate.opsForList().leftPush("allArticleId",oldArticle.getArticleId());
            redisTemplate.opsForList().leftPush("article_type_",oldArticle.getArcType().getArcTypeId(),oldArticle.getArticleId());
        }
        map.put("errorNo", 0);
        return map;
    }

    @ResponseBody
    @RequestMapping("/genAllIndex")
    @RequiresPermissions(value = "生成所有资源信息索引")
    public boolean genAllIndex(){
        List<Article> articleList = articleService.listStatePass();
        if (articleList == null || articleList.size() == 0){
            return false;
        }
        for(Article article : articleList){

            try {
                article.setContentNoTag(StringUtil.stripHtml(article.getContent()));
                articleIndex.addIndex(article);
            } catch (Exception e) {
                e.printStackTrace();
                return  false;
            }

        }
        return  true;
    }

    @ResponseBody
    @RequestMapping("/updateHotState")
    @RequiresPermissions(value = "修改热门资源状态")
    public Map<String, Object>  updateHotState(Integer articleId, boolean isHot) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        Article oldArticle = articleService.getById(articleId);
        oldArticle.setHot(isHot);
        articleService.save(oldArticle);
        map.put("success",true);
        return  map;
    }

    @ResponseBody
    @RequestMapping("/updateFreeState")
    @RequiresPermissions(value = "修改免费资源状态")
    public Map<String, Object>  updateFreeState(Integer articleId, boolean isFree) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        Article oldArticle = articleService.getById(articleId);
        oldArticle.setFree(isFree);
        articleService.save(oldArticle);
        map.put("success",true);
        return  map;
    }

}