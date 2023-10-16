package com.xsaxc.code.service;

import com.xsaxc.code.entity.ArcType;
import org.springframework.data.domain.Sort.Direction;

import java.util.List;
import java.util.Map;

/**
 * @author 师晓峰
 * @version V1.0
 * @date 2023/10/1 0:55
 * @Description:
 *      资源类型ArcTypeService 接口
 */
public interface ArcTypeService {

    /**
     * 分页查询资源类型信息
     *
     * @param page   当前页面
     * @param pageSize  每页记录数
     * @param direction  排序规则
     * @param properties  排序字段
     * @return
     */
    public List<ArcType> list(Integer page,Integer pageSize,Direction direction,String... properties);

    /**
     * 查询资源类型信息
     *
     * @param direction  排序规则
     * @param properties  排序字段
     * @return
     */
    public List list(Direction direction,String... properties);

    /**
     * 获取总记录数
     * @return
     */
    public Long getCount();

    /**
     * 添加或修改资源类型
     * @param arcType
     */
    public void save(ArcType arcType);

    /**
     * 根据id删除资源信息
     * @param id
     */
    public void delete(Integer id);

    /**
     * 根据id查找资源信息
     * @param id
     * @return
     */
    public ArcType getById(Integer id);



}
