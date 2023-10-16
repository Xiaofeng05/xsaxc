package com.xsaxc.code.service.impl;

import com.xsaxc.code.entity.Comment;
import com.xsaxc.code.respository.CommentRepository;
import com.xsaxc.code.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

/**
 * @author 师晓峰
 * @version V1.0
 * @date 2023/10/11 20:07
 * @Description: 评论CommentService实现
 */
@Service("commentService")
@Transactional(rollbackOn = Exception.class)
public class CommentServiceImpl implements CommentService {


    @Autowired
    private CommentRepository commentRepository;

    /**
     * 保存评论
     *
     * @param comment
     */
    @Override
    public void save(Comment comment) {
        commentRepository.save(comment);
    }

    /**
     * 获取分页评论信息
     *
     * @param s_Comment  评论实体
     * @param page       当前页
     * @param pageSize   每页记录数
     * @param direction  条件
     * @param properties 参数
     * @return
     */
    @Override
    public Page<Comment> list(Comment s_Comment, Integer page, Integer pageSize, Sort.Direction direction, String... properties) {

        return commentRepository.findAll(new Specification<Comment>() {
            @Override
            public Predicate toPredicate(Root<Comment> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate predicate = getPredicate(root, criteriaBuilder, s_Comment);
                return predicate;
            }
        }, PageRequest.of(page - 1, pageSize, direction, properties));

    }

    /**
     * 获取记录总数
     *
     * @return 获取记录总数
     */
    @Override
    public Long getTotal(Comment s_Comment) {
        return commentRepository.count(new Specification<Comment>() {
            @Override
            public Predicate toPredicate(Root<Comment> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate predicate = getPredicate(root, criteriaBuilder, s_Comment);
                return predicate;
            }
        });
    }

    /**
     * 根据id 获取评论对象
     *
     * @param commentId
     * @return
     */
    @Override
    public Comment getById(Integer commentId) {
        return commentRepository.getOne(commentId);
    }


    /**
     * 根据id 删除评论对象
     *
     * @param commentId
     */
    @Override
    public void delete(Integer commentId) {
        commentRepository.deleteById(commentId);
    }

    /**
     * 删除指定资源下的所有评论
     *
     * @param articleId
     */
    @Override
    public void deleteByArticleId(Integer articleId) {
        commentRepository.deleteByArticleId(articleId);
    }

    private Predicate getPredicate(Root<Comment> root, CriteriaBuilder criteriaBuilder, Comment s_Comment) {
        Predicate predicate = criteriaBuilder.conjunction();
        if (s_Comment != null) {
            if (s_Comment.getState() != 0){
                predicate.getExpressions().add(criteriaBuilder.equal(root.get("state"), s_Comment.getState()));
            }
            if (s_Comment.getArticle() != null && s_Comment.getArticle().getArticleId() != null) {
                predicate.getExpressions().add(criteriaBuilder.equal(root.get("article").get("articleId"), s_Comment.getArticle().getArticleId()));
            }

        }
        return predicate;
    }
}
