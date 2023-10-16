package com.xsaxc.code.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * @author 师晓峰
 * @version V1.0
 * @date 2023/10/14 17:27
 * @Description:
 *      Message实体
 */

@Entity
@Table(name = "message")
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "header", "fieldHandle"})
public class Message {

    /**
     * 消息id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer messageId;

    /**
     * 消息内容
     */
    @Column(length = 1024)
    private String content;

    /**
     * 消息原因
     */
    @Column(length = 100)
    private String cause;


    /**
     * 消息发布时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date publishDate;


    /**
     * 是否查看
     */
    private Boolean isSee = false;

    /**
     * 消息用户
     */
    @ManyToOne()
    @JoinColumn(name="userId")
    private User user;

    /**
     * 消息对应资源
     */

    private Integer articleId;


    public Integer getArticleId() {
        return articleId;
    }

    public void setArticleId(Integer articleId) {
        this.articleId = articleId;
    }

    public Integer getMessageId() {
        return messageId;
    }

    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    public Boolean getSee() {
        return isSee;
    }

    public void setSee(Boolean see) {
        isSee = see;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
