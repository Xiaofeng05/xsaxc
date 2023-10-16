package com.xsaxc.code.controller;

import com.xsaxc.code.entity.ArcType;
import com.xsaxc.code.entity.Article;
import com.xsaxc.code.lucene.ArticleIndex;
import com.xsaxc.code.service.ArcTypeService;
import com.xsaxc.code.service.ArticleService;
import com.xsaxc.code.util.Const;
import com.xsaxc.code.util.HTMLUtil;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * @author 师晓峰
 * @version V1.0
 * @date 2023/10/6 21:49
 * @Description: 资源类型控制器 ArticleController
 */
@RestController
@RequestMapping("/article")
public class ArticleController {

    @Autowired
    private ArcTypeService arcTypeService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private ArticleIndex articleIndex;


    /**
     * 按照资源类型分页查询资源列表
     *
     * @param type
     * @param currentPage
     * @return
     */
    @RequestMapping("/{type}/{currentPage}")
    public ModelAndView index(@PathVariable(value = "type", required = false) String type,
                              @PathVariable(value = "currentPage", required = false) Integer currentPage) {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("index");
        List<ArcType> arcTypeList = arcTypeService.list(Sort.Direction.ASC, "sort");

        // 类型的HTML代码
        mav.addObject("actTypeStr", HTMLUtil.getArcTypeStr(type, arcTypeList));

        // 资源列表
        Map<String, Object> map = articleService.list(type, currentPage, Const.PAGE_SIZE);
        mav.addObject("articleList", map.get("data"));
        // 分页HTML代码
        mav.addObject("pageStr", HTMLUtil.getLayPage("http://120.46.82.82:8080/code/article/" + type, Integer.parseInt(String.valueOf(map.get("count"))), currentPage, "该分类还没有数据..."));

        return mav;
    }

    /**
     * 关键词搜索
     *
     * @param keywords
     * @param page
     * @return
     */
    @RequestMapping("/search")
    public ModelAndView search(String keywords, @RequestParam(value = "page", required = false) Integer page) throws ParseException, InvalidTokenOffsetsException, org.apache.lucene.queryparser.classic.ParseException, IOException {


        ModelAndView mav = new ModelAndView();
        if (page == null) {
            page = 1;
        }

        List<ArcType> arcTypeList = arcTypeService.list(Sort.Direction.ASC, "sort");

        // 类型的HTML代码
        mav.addObject("actTypeStr", HTMLUtil.getArcTypeStr("all", arcTypeList));

        // 资源列表
        List<Article> articleList = articleIndex.search(keywords);
        Integer toIndex = articleList.size() >= page * Const.PAGE_SIZE ? page * Const.PAGE_SIZE : articleList.size();

        mav.addObject("articleList", articleList.subList((page - 1) * Const.PAGE_SIZE, toIndex));
        mav.addObject("keywords", keywords);

        // 分页HTML代码
        int totalPage = articleList.size() % Const.PAGE_SIZE == 0 ? articleList.size() / Const.PAGE_SIZE : (articleList.size() / Const.PAGE_SIZE) + 1;
        String targetUrl = "http://120.46.82.82:8080/code/article/search?keywords=" + keywords;
        String msg = "没有关键字是 \"<font style=\"border:0px;color:red;font-weight:bold;padding-left:3px;padding-right:3px\">" + keywords + "</font>\" 的相关资源，请联系管理员";
        mav.addObject("pageStr", HTMLUtil.getLayPageByLucene(targetUrl, totalPage, page, msg));
        mav.setViewName("index");
        return mav;
    }

    @RequestMapping("/detail/{articleId}")
    public ModelAndView detail(@PathVariable(value = "articleId", required = false)String articleId) throws IOException, org.apache.lucene.queryparser.classic.ParseException {
        ModelAndView mav = new ModelAndView();
        String replace = articleId.replace(".html", "");
        articleService.updateClick(Integer.parseInt(replace));
        Article article = articleService.getById(Integer.parseInt(replace));
        if (article.getState() != 2) {
            return null;
        }


        List<ArcType> arcTypeList = arcTypeService.list(Sort.Direction.ASC, "sort");
        // 类型的HTML代码
        mav.addObject("actTypeStr", HTMLUtil.getArcTypeStr(article.getArcType().getArcTypeId().toString(), arcTypeList));
        // todo:找相似的资源
        // 敏感词
        String keywords = article.getName()
                .trim()
                .replace("视频", "")
                .replace("下载", "")
                .replace("教程", "");
        List<Article> articleList = articleIndex.searchNoHighlighter(keywords);

        if (articleList != null && articleList.size() > 0){
            mav.addObject("similarityArticleList", articleList);
        }

        mav.addObject("article", article);
        mav.setViewName("detail");
        return mav;
    }


    @ResponseBody
    @RequestMapping("/isFree")
    public boolean isFree(Integer articleId){
        Article article = articleService.getById(articleId);
        return  article.isFree();
    }

}
