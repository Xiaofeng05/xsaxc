package com.xsaxc.code.service.impl;

import com.xsaxc.code.entity.UserDownload;
import com.xsaxc.code.respository.UserDownloadRepository;
import com.xsaxc.code.service.UserDownloadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

/**
 * @author 师晓峰
 * @version V1.0
 * @date 2023/10/11 13:47
 * @Description:
 *      用户下载 Service 实现类
 */
@Service("userDownloadService")
@Transactional(rollbackOn = Exception.class)
public class UserDownloadServiceImpl implements UserDownloadService {

    @Autowired
    private UserDownloadRepository userDownloadRepository;


    /**
     * 查询某个用户某个资源的下载次数
     *
     * @param userId
     * @param articleId
     * @return
     */
    @Override
    public Integer getCountBuUserIdAndArticleId(Integer userId, Integer articleId) {
        return userDownloadRepository.getCountBuUserIdAndArticleId(userId,articleId);
    }

    /**
     * 分页查询某个用户下载的所有资源
     *
     * @param userId
     * @param page
     * @param pageSize
     * @param direction
     * @param properties
     * @return
     */
    @Override
    public Page<UserDownload> list(Integer userId, Integer page, Integer pageSize, Sort.Direction direction, String... properties) {
        return userDownloadRepository.findAll(new Specification<UserDownload>() {
            @Override
            public Predicate toPredicate(Root<UserDownload> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate predicate = criteriaBuilder.conjunction();
                if (userId != null){
                    predicate.getExpressions().add(criteriaBuilder.equal(root.get("user").get("userId"),userId));
                }
                return predicate;
            }
        }, PageRequest.of(page - 1,pageSize,direction,properties));
    }

    /**
     * 统计某个用户下载的资源数
     *
     * @param userId
     * @return
     */
    @Override
    public Long getCount(Integer userId) {
        return userDownloadRepository.count(new Specification<UserDownload>() {
            @Override
            public Predicate toPredicate(Root<UserDownload> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate predicate = criteriaBuilder.conjunction();
                if (userId != null){
                    predicate.getExpressions().add(criteriaBuilder.equal(root.get("userId"),userId));
                }
                return predicate;
            }
        });
    }

    /**
     * 添加或者修改某个用户下载的信息
     *
     * @param userDownload
     */
    @Override
    public void save(UserDownload userDownload) {
        userDownloadRepository.save(userDownload);
    }

    /**
     * 根据id删除资源信息
     *
     * @param article_id
     */
    @Override
    public void deleteByArticleId(Integer article_id) {
        userDownloadRepository.deleteByArticleId(article_id);
    }
}
