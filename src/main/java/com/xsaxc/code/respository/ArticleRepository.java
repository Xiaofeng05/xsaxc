package com.xsaxc.code.respository;

import com.xsaxc.code.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author 师晓峰
 * @version V1.0
 * @date 2023/10/1 0:54
 * @Description: 资源类型 ArticleRepository 接口
 */
public interface ArticleRepository extends JpaRepository<Article, Integer>, JpaSpecificationExecutor<Article> {


    /**
     * 点击数 + 1
     *
     * @param articleId
     */
    @Query(value = "update article set click = click + 1 where article_id = ?1", nativeQuery = true)
    @Modifying
    public void updateClick(Integer articleId);


    /**
     * 今日发布资源总数
     *
     * @return
     */
    @Query(value = "select count(*) from article where TO_DAYS(publish_date) = TO_DAYS(NOW())", nativeQuery = true)
    public Integer todayPublish();

    /**
     * 未审核资源总数
     *
     * @return
     */
    @Query(value = "select count(*) from article where state = 1", nativeQuery = true)
    public Integer noAudit();


    /**
     * 根据id删除资源信息
     *
     * @param article_id
     */
    @Override
    @Query(value = "delete from article where article_id = ?1", nativeQuery = true)
    @Modifying
    public void deleteById(Integer article_id);

    /**
     * 根据资源id查询资源类型id
     *
     * @param article_id
     * @return
     */
    @Query(value = "select arc_type_id from article where article_id = ?1", nativeQuery = true)
    public Integer getArcTypeIdByArticleId(Integer article_id);

    /**
     * 4 条 最新资源
     * 根据时间排序
     *
     * @param num
     * @return
     */
    @Query(value = "select * from article where state = 2 order by publish_date limit ?1", nativeQuery = true)
    public List<Article> getNewArticle(Integer num);


    /**
     * 4 条 热门资源
     * 根据点击量排序
     *
     * @param num
     * @return
     */
    @Query(value = "select * from article where state = 2 order by click limit ?1", nativeQuery = true)
    public List<Article> getHotArticle(Integer num);


    /**
     * 4 条 随机资源（热搜推荐）
     *
     * @param num
     * @return
     */
    @Query(value = "select * from article where state = 2 order by RAND() limit ?1", nativeQuery = true)
    public List<Article> getRandArticle(Integer num);

}
