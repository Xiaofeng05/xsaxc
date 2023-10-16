package com.xsaxc.code.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * @author 师晓峰
 * @version V1.0
 * @date 2023/10/1 0:38
 * @Description: 资源类型实体
 */
@Entity
@Table(name = "arcType")
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "header", "fieldHandle"})
public class ArcType implements Serializable {


    /**
     * 资源类型id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer arcTypeId;


    /**
     * 资源类型名称
     */
    @NotEmpty(message = "资源类型不能为空")
    @Column(length = 200)
    private String arcTypeName;


    /**
     * 资源描述
     */
    @Column(length = 1024)
    private String remark;


    /**
     * 排序
     */
    private Integer sort;

    public Integer getArcTypeId() {
        return arcTypeId;
    }

    public void setArcTypeId(Integer arcTypeId) {
        this.arcTypeId = arcTypeId;
    }

    public String getArcTypeName() {
        return arcTypeName;
    }

    public void setArcTypeName(String arcTypeName) {
        this.arcTypeName = arcTypeName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}
