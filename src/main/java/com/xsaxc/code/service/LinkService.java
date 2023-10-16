package com.xsaxc.code.service;

import com.xsaxc.code.entity.Link;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author 师晓峰
 * @version V1.0
 * @date 2023/10/13 18:17
 * @Description: 友情链接 Service 接口
 */
public interface LinkService {


    /**
     * 分页查询友情链接信息
     *
     * @param link       友情链接
     * @param page       当前页面
     * @param pageSize   每页记录数
     * @param direction  排序规则
     * @param properties 排序字段
     * @return
     */
    public List<Link> list(Link link, @RequestParam(value = "page", required = false) Integer page,
                           @RequestParam(value = "pageSize", required = false) Integer pageSize,
                           Direction direction, String... properties);


    /**
     * 分页查询友情链接信息
     *
     * @param page       当前页面
     * @param pageSize   每页记录数
     * @param direction  排序规则
     * @param properties 排序字段
     * @return
     */
    public List<Link> list(@RequestParam(value = "page", required = false) Integer page,
                           @RequestParam(value = "pageSize", required = false) Integer pageSize,
                           Direction direction, String... properties);

    /**
     * 查询友情链接信息
     *
     * @param direction  排序规则
     * @param properties 排序字段
     * @return
     */
    public List<Link> listALL(Direction direction, String... properties);

    /**
     * 获取总记录数
     *
     * @return
     */
    public Long getTotal();


    /**
     * 新增或者修改友情链接
     *
     * @param link
     */
    public void save(Link link);


    /**
     * 根据id删除友情链接
     *
     * @param linkId
     */
    public void delete(Integer linkId);

    /**
     * 根据id获取友情链接
     *
     * @param linkId
     */
    public Link getById(Integer linkId);

}
