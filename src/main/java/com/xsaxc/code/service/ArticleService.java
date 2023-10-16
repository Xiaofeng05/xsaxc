package com.xsaxc.code.service;

import com.xsaxc.code.entity.Article;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

/**
 * @author 师晓峰
 * @version V1.0
 * @date 2023/10/1 0:55
 * @Description:
 *      资源类型 ArticleService 接口
 */
public interface ArticleService {

    /**
     * 根据分页条件查询资源信息列表
     *
     * @param s_Article 资源类型实体
     * @param nickname 用户昵称
     * @param s_bpublicDate  开始时间
     * @param s_epublicDate  结束时间
     * @param page   当前页面
     * @param pageSize  一页多少数量
     * @param direction  排序条件
     * @param properties  排序字段
     * @return
     */
    public List<Article> list(Article s_Article, String nickname, String s_bpublicDate, String s_epublicDate, Integer page , Integer pageSize, Direction direction, String... properties);


    /**
     * 根据条件获取总记录数
     *
     * @param s_Article 资源类型实体
     * @param nickname 用户昵称
     * @param s_bpublicDate  开始时间
     * @param s_epublicDate  结束时间
     * @return
     */
    public Long getCount(Article s_Article, String nickname, String s_bpublicDate, String s_epublicDate);


    /**
     * 增加或者修改资源类型
     * @param Article
     */
    public void save(Article Article) throws Exception;



    /**
     * 根据主键id获取资源类型信息
     * @param id
     * @return
     */
    public Article getById(Integer id);

    /**
     * 分页类型查询所在资源
     * @param type  类型id
     * @param page 当前页
     * @param pageSize 每页的记录数
     * @return
     */
    public Map<String,Object> list(String type, Integer page, Integer pageSize);



    /**
     * 查询所有审核通过的资源信息列表
     * @return
     */
    public List<Article> listStatePass();

    /**
     * 点击数 + 1
     * @param articleId
     */
    public void updateClick(Integer articleId);

    /**
     * 根据id删除资源信息
     *
     * @param article_id
     */
    public void deleteById(Integer article_id);

    /**
     * 今日发布资源总数
     * @return
     */
    public Integer todayPublish();

    /**
     * 未审核资源总数
     * @return
     */
    public Integer noAudit();


    /**
     * 4 条 最新资源
     * 根据时间排序
     *
     * @param num
     * @return
     */
    public List<Article> getNewArticle(Integer num);
    /**
     * 4 条 热门资源
     * 根据点击量排序
     *
     * @param num
     * @return
     */
    public List<Article> getHotArticle(Integer num);


    /**
     * 4 条 随机资源（热搜推荐）
     *
     * @param num
     * @return
     */
    public List<Article> getRandArticle(Integer num);
}
