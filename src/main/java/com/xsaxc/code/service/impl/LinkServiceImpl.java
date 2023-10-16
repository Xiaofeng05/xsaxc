package com.xsaxc.code.service.impl;

import com.xsaxc.code.entity.Link;
import com.xsaxc.code.respository.LinkRepository;
import com.xsaxc.code.service.LinkService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * @author 师晓峰
 * @version V1.0
 * @date 2023/10/13 18:25
 * @Description:
 */
@Service("linkService")
public class LinkServiceImpl implements LinkService {

    @Autowired
    private LinkRepository linkRepository;


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
    @Override
    public List<Link> list(Link link, Integer page, Integer pageSize, Sort.Direction direction, String... properties) {
        return linkRepository.findAll(new Specification() {
            @Override
            public Predicate toPredicate(Root root, CriteriaQuery criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate predicate = criteriaBuilder.conjunction();
                if (link != null) {
                    if (StringUtils.isNotEmpty(link.getLinkName())) {
                        predicate.getExpressions().add(criteriaBuilder.like(root.get("linkName"), "%" + link.getLinkName() + "%"));
                    }

                }
                return predicate;
            }
        }, PageRequest.of(page - 1, pageSize, direction, properties)).getContent();
    }

    /**
     * 分页查询友情链接信息
     *
     * @param page       当前页面
     * @param pageSize   每页记录数
     * @param direction  排序规则
     * @param properties 排序字段
     * @return
     */
    @Override
    public List<Link> list(Integer page, Integer pageSize, Sort.Direction direction, String... properties) {
        return linkRepository.findAll(PageRequest.of(page - 1, pageSize, direction, properties)).getContent();
    }

    /**
     * 查询友情链接信息
     *
     * @param direction  排序规则
     * @param properties 排序字段
     * @return
     */
    @Override
    public List<Link> listALL(Sort.Direction direction, String... properties) {
        Sort sort = Sort.by(direction, properties);
        return linkRepository.findAll(sort);
    }

    /**
     * 获取总记录数
     *
     * @return
     */
    @Override
    public Long getTotal() {
        return linkRepository.count();
    }

    /**
     * 新增或者修改友情链接
     *
     * @param link
     */
    @Override
    public void save(Link link) {
        linkRepository.save(link);
    }

    /**
     * 根据id删除友情链接
     *
     * @param linkId
     */
    @Override
    public void delete(Integer linkId) {
        linkRepository.deleteById(linkId);
    }

    /**
     * 根据id获取友情链接
     *
     * @param linkId
     */
    @Override
    public Link getById(Integer linkId) {
        return linkRepository.getOne(linkId);
    }
}
