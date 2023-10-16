package com.xsaxc.code.service.impl;

import com.xsaxc.code.entity.ArcType;
import com.xsaxc.code.entity.Article;
import com.xsaxc.code.lucene.ArticleIndex;
import com.xsaxc.code.respository.ArticleRepository;
import com.xsaxc.code.run.StartupRunner;
import com.xsaxc.code.service.ArcTypeService;
import com.xsaxc.code.service.ArticleService;
import com.xsaxc.code.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 师晓峰
 * @version V1.0
 * @date 2023/10/2 16:08
 * @Description: 实现 ArticleService 接口
 */
@Service("articleService")
@Transactional(rollbackOn = Exception.class)
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;


    private RedisSerializer redisSerializer = new StringRedisSerializer();

    @Autowired
    private ArcTypeService arcTypeService;

    @Autowired
    private ArticleIndex articleIndex;

    @Autowired
    @Lazy
    private StartupRunner startupRunner;

    @Override
    public List<Article> list(Article s_Article, String nickname, String s_bpublishDate, String s_epublishDate, Integer page, Integer pageSize, Sort.Direction direction, String... properties) {
        Page<Article> articlePage = articleRepository.findAll(new Specification<Article>() {
            @Override
            public Predicate toPredicate(Root<Article> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                return getPredicate(root, cb, s_bpublishDate, s_epublishDate, nickname, s_Article);
            }
        }, PageRequest.of(page - 1, pageSize, direction, properties));

        return articlePage.getContent();
    }

    @Override
    public Long getCount(Article s_Article, String nickname, String s_bpublishDate, String s_epublishDate) {
        Long count = articleRepository.count(new Specification<Article>() {
            @Override
            public Predicate toPredicate(Root<Article> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                return getPredicate(root, cb, s_bpublishDate, s_epublishDate, nickname, s_Article);
            }
        });
        return count;
    }

    private Predicate getPredicate(Root<Article> root, CriteriaBuilder cb, String s_bpublishDate, String s_epublishDate, String nickname, Article s_Article) {
        Predicate predicate = cb.conjunction();
        // 开始时间
        if (StringUtil.isNotEmpty(s_bpublishDate)) {
            predicate.getExpressions().add(cb.greaterThanOrEqualTo(root.get("publishDate").as(String.class), s_bpublishDate));
        }
        // 结束时间
        if (StringUtil.isNotEmpty(s_epublishDate)) {
            predicate.getExpressions().add(cb.greaterThanOrEqualTo(root.get("publishDate").as(String.class), s_epublishDate));
        }
        // 用户昵称
        if (StringUtil.isNotEmpty(nickname)) {
            predicate.getExpressions().add(cb.like(root.get("user").get("nickname"), "%" + nickname + "%"));
        }

        if (s_Article != null) {
            if (StringUtil.isNotEmpty(s_Article.getName())) {
                // 资源标题
                predicate.getExpressions().add(cb.like(root.get("name"), "%" + s_Article.getName() + "%"));
            }
            if (s_Article.isHot()) {
                // 是否热门
                predicate.getExpressions().add(cb.equal(root.get("isHot"), 1));
            }
            if (s_Article.getArcType() != null && s_Article.getArcType().getArcTypeId() != null) {
                // 资源类型
                predicate.getExpressions().add(cb.equal(root.get("arcType").get("arcTypeId"), s_Article.getArcType().getArcTypeId()));
            }
            if (s_Article.getUser() != null && s_Article.getUser().getUserId() != null) {
                // 用户
                predicate.getExpressions().add(cb.equal(root.get("user").get("userId"), s_Article.getUser().getUserId()));
            }
            if (s_Article.getState() != null) {
                // 审核状态
                predicate.getExpressions().add(cb.equal(root.get("state"), s_Article.getState()));
            }
            if (!s_Article.isUseful()) {
                predicate.getExpressions().add((cb.equal(root.get("isUseful"), false)));
            }
        }

        return predicate;
    }

    @Override
    public void save(Article article) throws Exception {
        if (article.getState() == 2) {
            redisTemplate.setKeySerializer(redisSerializer);
            // 把审核通过的资源放入redis中
            redisTemplate.opsForValue().set("article_" + article.getArticleId(), article);
            // 刷新一下缓存
            startupRunner.loadData();

        }
        articleRepository.save(article);
    }

    @Override
    public Article getById(Integer id) {
        if (redisTemplate.hasKey("article_" + id)) {
            return (Article) redisTemplate.opsForValue().get("article_" + id);
        } else {
            Article article = articleRepository.getOne(id);

            if (article.getState() == 2) {
                // 把审核通过的资源放入redis中
                redisTemplate.setKeySerializer(redisSerializer);
                redisTemplate.opsForValue().set("article_" + article.getArticleId(), article);
            }
            return article;
        }

    }


    @Override
    public Map<String, Object> list(String type, Integer page, Integer pageSize) {
        // 复杂逻辑建议先写步骤
        // 1.初始化redis模板（先从redis中取  | 初始化返回值
        redisTemplate.setKeySerializer(redisSerializer);
        ValueOperations<Object, Object> opsForValue = redisTemplate.opsForValue();
        ListOperations<Object, Object> opsForList = redisTemplate.opsForList();
        Map<String, Object> map = new HashMap<String, Object>();
        List<Article> tempList = new ArrayList<Article>();
        // 2.先判断缓存中是否有
        Boolean flag = redisTemplate.hasKey("allArticleId");
        // 3.如果没有去数据库查询，放进来
        if (!flag) {
            // 3.1 如果有 去遍历资源列表(审核通过的）
            List<Article> articleList = listStatePass();
            for (Article article : articleList) {
                // 3.2 将每一项资源放到redis中
                opsForValue.set("article_" + article.getArticleId(), article);
                // 3.3 将每一个资源的id推入到redis的allArticleId列表中
                opsForList.rightPush("allArticleId", article.getArticleId());
                // 3.4 遍历每一个资源类型，将该资源推入相应的redis资源类型列表中
                List<ArcType> arcTypeList = arcTypeService.list(Direction.ASC, "sort");
                for (ArcType arcType : arcTypeList) {
                    if (article.getArcType().getArcTypeId().intValue() == arcType.getArcTypeId().intValue()) {
                        opsForList.rightPush("article_type_" + arcType.getArcTypeId(), article.getArticleId());
                    }
                }
            }
        }

        // 4.分页资源列表并且返回当前页面  1  10 [0 9]
        long start = (page - 1) * pageSize;
        long end = pageSize * page - 1;
        List idsList;
        long count;
        // 首页
        if("all".equals(type)){
            idsList = opsForList.range("allArticleId",start,end);
            count = opsForList.size("allArticleId");
        }else{
            idsList = opsForList.range("article_type_"+type,start,end);
            count = opsForList.size("article_type_"+ type);
        }
        // 遍历id
        for(Object id : idsList){
            tempList.add((Article) opsForValue.get("article_"+id));
        }
        map.put("data", tempList);
        map.put("count", count);

        return map;
    }

    @Override
    public List<Article> listStatePass() {
        return articleRepository.findAll(new Specification<Article>() {
            @Override
            public Predicate toPredicate(Root<Article> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate predicate = criteriaBuilder.conjunction();
                predicate.getExpressions().add(criteriaBuilder.equal(root.get("state"), 2));
                return predicate;
            }
        }, Sort.by(Direction.DESC, "publishDate"));
    }

    @Override
    public void updateClick(Integer articleId) {
        articleRepository.updateClick(articleId);
        Article article = articleRepository.getOne(articleId);
        if (article.getState() == 2) {
            // 把审核通过的资源放入redis中
            redisTemplate.setKeySerializer(redisSerializer);
            redisTemplate.opsForValue().set("article_" + article.getArticleId(), article);
        }
    }

    /**
     * 根据id删除资源信息
     *
     * @param article_id
     */
    @Override
    public void deleteById(Integer article_id) {
        redisTemplate.opsForList().remove("allArticleId",0, article_id);
        int arcTypeId = articleRepository.getArcTypeIdByArticleId(article_id);
        redisTemplate.opsForList().remove("article_type_"+arcTypeId,0,article_id);
        redisTemplate.delete("article_" + article_id);
        articleRepository.deleteById(article_id);
    }

    /**
     * 今日发布资源总数
     *
     * @return
     */
    @Override
    public Integer todayPublish() {
        return articleRepository.todayPublish();
    }

    /**
     * 未审核资源总数
     *
     * @return
     */
    @Override
    public Integer noAudit() {
        return articleRepository.noAudit();
    }

    /**
     * 4 条 最新资源
     * 根据时间排序
     *
     * @param num
     * @return
     */
    @Override
    public List<Article> getNewArticle(Integer num) {
        return articleRepository.getNewArticle(num);
    }

    /**
     * 4 条 热门资源
     * 根据点击量排序
     *
     * @param num
     * @return
     */
    @Override
    public List<Article> getHotArticle(Integer num) {
        return articleRepository.getHotArticle(num);
    }

    /**
     * 4 条 随机资源（热搜推荐）
     *
     * @param num
     * @return
     */
    @Override
    public List<Article> getRandArticle(Integer num) {
        return articleRepository.getRandArticle(num);
    }
}
