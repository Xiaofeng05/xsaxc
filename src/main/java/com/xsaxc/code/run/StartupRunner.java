package com.xsaxc.code.run;

import com.xsaxc.code.entity.ArcType;
import com.xsaxc.code.service.ArcTypeService;
import com.xsaxc.code.service.ArticleService;
import com.xsaxc.code.service.LinkService;
import com.xsaxc.code.util.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import java.util.List;

/**
 * @author 师晓峰
 * @version V1.0
 * @date 2023/10/5 0:17
 * @Description: 存放下applicationContext中
 */
@Component("startupRunner")
public class StartupRunner implements CommandLineRunner {

    @Autowired
    private ServletContext application;

    @Autowired
    private ArcTypeService arcTypeService;

    @Autowired
    private LinkService linkService;

    @Autowired
    @Lazy
    private ArticleService articleService;

    @Override
    public void run(String... args) throws Exception {
        loadData();
    }


    /**
     * 加载数据到application 缓存中
     */
    public void loadData() {

        /**
         * 资源类型
         */
        application.setAttribute(Const.ARC_TYPE_LIST, arcTypeService.list(Direction.ASC, "sort"));
        /**
         * 所有友情链接
         */
        application.setAttribute(Const.LINK_LIST, linkService.listALL(Direction.ASC, "sort"));

        /**
         * 4 条 最新资源
         */

        application.setAttribute(Const.NEW_ARTICLE, articleService.getNewArticle(4));

        /**
         * 4 条 热门资源
         */

        application.setAttribute(Const.HOT_ARTICLE, articleService.getHotArticle(4));

        /**
         * 4 条 随机资源（热搜推荐）
         */
        application.setAttribute(Const.RAND_ARTICLE, articleService.getRandArticle(4));

    }
}
