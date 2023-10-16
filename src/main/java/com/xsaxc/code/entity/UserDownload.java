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
 * @date 2023/10/11 13:22
 * @Description:
 */
@Entity
@Table(name = "userDownload")
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "header", "fieldHandle"})
public class UserDownload implements Serializable {

    /**
     * 用户下载id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userDownloadId;

    /**
     * 下载时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date downloadDate;

    /**
     * 下载用户
     */
    @ManyToOne()
    @JoinColumn(name="userId")
    private User user;

    /**
     * 下载资源
     */
    @ManyToOne()
    @JoinColumn(name="articleId")
    private Article article;

    public Integer getUserDownloadId() {
        return userDownloadId;
    }

    public void setUserDownloadId(Integer userDownloadId) {
        this.userDownloadId = userDownloadId;
    }

    public Date getDownloadDate() {
        return downloadDate;
    }

    public void setDownloadDate(Date downloadDate) {
        this.downloadDate = downloadDate;
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
