package com.xsaxc.code.service;

import com.xsaxc.code.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

/**
 * @author 师晓峰
 * @version V1.0
 * @date 2023/10/11 17:52
 * @Description: 评论Service
 */
public interface CommentService {


    /**
     * 保存评论
     *
     * @param comment
     */
    public void save(Comment comment);


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
    public Page<Comment> list(Comment s_Comment, Integer page, Integer pageSize, Sort.Direction direction, String... properties);


    /**
     * 获取记录总数
     *
     * @param s_Comment
     * @return 获取记录总数
     */
    public Long getTotal(Comment s_Comment);


    /**
     * 根据id 获取评论对象
     *
     * @param commentId
     * @return
     */
    public Comment getById(Integer commentId);


    /**
     * 根据id 删除评论对象
     *
     * @param commentId
     */
    public void delete(Integer commentId);

    /**
     * 删除指定资源下的所有评论
     * @param articleId
     */
    public void deleteByArticleId(Integer articleId);

}
