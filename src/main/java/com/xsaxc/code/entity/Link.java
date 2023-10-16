package com.xsaxc.code.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author 师晓峰
 * @version V1.0
 * @date 2023/10/13 18:11
 * @Description:
 *      友情链接实体类
 */
@Entity
@Table(name = "link")
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "header", "fieldHandle"})

public class Link implements Serializable {
    /**
     * 友情链接id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer linkId;

    /**
     * 友情链接名称
     */
    @Column(length = 200)
    private String linkName;

    /**
     * 友情链接地址
     */
    @Column(length = 200)
    private String linkUrl;

    /**
     * 友情链接邮件
     */
    @Column(length = 200)
    private String linkEmail;

    /**
     * 友情链接排序
     */
    private Integer  sort;

    public Integer getLinkId() {
        return linkId;
    }

    public void setLinkId(Integer linkId) {
        this.linkId = linkId;
    }

    public String getLinkName() {
        return linkName;
    }

    public void setLinkName(String linkName) {
        this.linkName = linkName;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public String getLinkEmail() {
        return linkEmail;
    }

    public void setLinkEmail(String linkEmail) {
        this.linkEmail = linkEmail;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}
