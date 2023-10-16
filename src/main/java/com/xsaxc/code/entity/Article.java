package com.xsaxc.code.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Date;

/**
 * @author 师晓峰
 * @version V1.0
 * @date 2023/10/2 15:38
 * @Description:
 *      Article实体
 */
@Entity
@Table(name = "article")
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "header", "fieldHandle"})
public class Article implements Serializable {

    /**
     * 资源id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer articleId;


    /**
     * 资源名称
     */
    @NotEmpty(message = "资源不能为空")
    @Column(length = 200)
    private String name;


    /**
     * 发布时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date publishDate;


    /**
     * 发布时间字符串
     */
    @Transient
    private String publishDateStr;

    /**
     * 所属用户
     */
    @ManyToOne
    @JoinColumn(name="userId")
    private User user;

    /**
     * 所属资源类型
     */
    @ManyToOne
    @JoinColumn(name="arcTypeId")
    private ArcType arcType;


    /**
     * 是否免费
     *      true 是 false 否
     */
    private boolean isFree = false;

    /**
     * 积分
     */
    private Integer points;

    /**
     * 资源内容
     */
    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;

    /**
     * 资源内容 （没有网页标签）
     *  lucene 用
     */
    @Transient
    private String contentNoTag;


    /**
     * 资源链接
     */
    @Column(length = 200)
    private String download;

    /**
     * 资源密码
     */
    @Column(length = 20)
    private String password;


    /**
     * 是否热门
     *      true 是 false 否
     */
    private boolean isHot = false;

    /**
     * 状态
     *      状态：1未审核；2审核通过；3审核驳回
     */
    private Integer state;


    /**
     * 驳回原因
     */
    private String reason;

    /**
     * 审核时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date checkDate;

    /**
     * 点击数
     */
    private Integer click;


    /**
     * 关键字
     */
    @Column(length = 200)
    private String keywords;

    /**
     * 描述
     */
    @Column(length = 200)
    private String description;

    /**
     * 是否有用
     *      true 是 false 否
     */
    private boolean isUseful = true;


    public Integer getArticleId() {
        return articleId;
    }

    public void setArticleId(Integer articleId) {
        this.articleId = articleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    public String getPublishDateStr() {
        return publishDateStr;
    }

    public void setPublishDateStr(String publishDateStr) {
        this.publishDateStr = publishDateStr;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ArcType getArcType() {
        return arcType;
    }

    public void setArcType(ArcType arcType) {
        this.arcType = arcType;
    }

    public boolean isFree() {
        return isFree;
    }

    public void setFree(boolean free) {
        isFree = free;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContentNoTag() {
        return contentNoTag;
    }

    public void setContentNoTag(String contentNoTag) {
        this.contentNoTag = contentNoTag;
    }

    public String getDownload() {
        return download;
    }

    public void setDownload(String download) {
        this.download = download;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isHot() {
        return isHot;
    }

    public void setHot(boolean hot) {
        isHot = hot;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Date getCheckDate() {
        return checkDate;
    }

    public void setCheckDate(Date checkDate) {
        this.checkDate = checkDate;
    }

    public Integer getClick() {
        return click;
    }

    public void setClick(Integer click) {
        this.click = click;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isUseful() {
        return isUseful;
    }

    public void setUseful(boolean useful) {
        isUseful = useful;
    }
}
