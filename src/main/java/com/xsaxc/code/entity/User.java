package com.xsaxc.code.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Date;

/**
 * @author 师晓峰
 * @version V1.0
 * @date 2023/9/29 16:34
 * @Description: 用户实体
 */
@Entity
@Table(name = "user")
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "header", "fieldHandle"})
public class User implements Serializable {

    /**
     * 用户id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    /**
     * QQ用户唯一表示
     */
    @Column(length = 200)
    private String openId;

    /**
     * 用户昵称
     */
    @NotEmpty(message = "用户昵称不能为空")
    @Column(length = 200)
    private String nickname;

    /**
     * 用户名
     */
    @NotEmpty(message = "用户名不能为空")
    @Column(length = 100)
    private String userName;

    /**
     * 用户密码
     */
    @NotEmpty(message = "用户密码不能为空")
    @Column(length = 100)
    private String password;

    /**
     * 用户邮箱
     */
    @NotEmpty(message = "用户邮箱不能为空")
    @Column(length = 200)
    @Email(message = "邮箱地址格式有误")
    private String email;

    /**
     * 用户头像
     */
    @Column(length = 400)
    private String headPortrait;


    /**
     * 用户性别
     */
    @Column(length = 50)
    private String sex;

    /**
     * 用户积分
     */
    private Integer points = 0;

    /**
     * 是否vip
     */

    private Boolean isVip = false;

    /**
     * vip等级
     */
    private Integer vipGrade = 0;

    /**
     * 是否被封禁
     */
    private Boolean isOff = false;


    /**
     * 用户角色：
     *  管理员 会员
     */
    private String roleName = "会员";

    /**
     * 用户注册时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date registerDate;

    /**
     * 用户最后登录时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastLoginTime;

    /**
     * 消息数量
     * @Transient 表示非数据库字段
     */
    @Transient
    private Integer messageCount;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHeadPortrait() {
        return headPortrait;
    }

    public void setHeadPortrait(String headPortrait) {
        this.headPortrait = headPortrait;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Boolean getVip() {
        return isVip;
    }

    public void setVip(Boolean vip) {
        isVip = vip;
    }

    public Integer getVipGrade() {
        return vipGrade;
    }

    public void setVipGrade(Integer vipGrade) {
        this.vipGrade = vipGrade;
    }

    public Boolean getOff() {
        return isOff;
    }

    public void setOff(Boolean off) {
        isOff = off;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public Integer getMessageCount() {
        return messageCount;
    }

    public void setMessageCount(Integer messageCount) {
        this.messageCount = messageCount;
    }
}
