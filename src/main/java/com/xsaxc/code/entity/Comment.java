package com.xsaxc.code.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author 师晓峰
 * @version V1.0
 * @date 2023/10/11 17:43
 * @Description:
 *      评论实体
 */
@Entity
@Table(name = "comment")
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "header", "fieldHandle"})
public class Comment implements Serializable {


    /**
     * 评论Id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer commentId;


    /**
     * 评论时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date commentDate;

    /**
     * 评论内容
     */
    @Column(length = 1024)
    private String content;

    /**
     * 评论状态
     */
    private Integer state = 0;

    /**
     * 评论用户
     */
    @ManyToOne()
    @JoinColumn(name="userId")
    private User user;

    /**
     * 评论对应资源
     */
    @ManyToOne()
    @JoinColumn(name="articleId")
    private Article article;


    public Integer getCommentId() {
        return commentId;
    }

    public void setCommentId(Integer commentId) {
        this.commentId = commentId;
    }

    public Date getCommentDate() {
        return commentDate;
    }

    public void setCommentDate(Date commentDate) {
        this.commentDate = commentDate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }
}
