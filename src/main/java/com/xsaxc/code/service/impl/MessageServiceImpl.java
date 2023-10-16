package com.xsaxc.code.service.impl;

import com.xsaxc.code.entity.Message;
import com.xsaxc.code.respository.MessageRepository;
import com.xsaxc.code.service.MessageService;
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
import java.util.List;

/**
 * @author 师晓峰
 * @version V1.0
 * @date 2023/10/14 17:46
 * @Description:
 */
@Service("messageService")
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageRepository messageRepository;

    /**
     * 分页查询消息列表
     *
     * @param userId     用户id
     * @param page       当前页
     * @param pageSize   每页记录数
     * @param direction  条件
     * @param properties 参数
     * @return
     */
    @Override
    public Page<Message> list(Integer userId, Integer page, Integer pageSize, Sort.Direction direction, String... properties) {
        return messageRepository.findAll(new Specification<Message>() {
            @Override
            public Predicate toPredicate(Root<Message> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate predicate = criteriaBuilder.conjunction();
                if (userId != null){
                    predicate.getExpressions().add(criteriaBuilder.equal(root.get("user").get("userId"),userId));
                }
                return predicate;
            }
        }, PageRequest.of(page - 1,pageSize,direction,properties));

    }

    /**
     * 根据用户id获取总记录数
     *
     * @param userId
     * @return
     */
    @Override
    public Long getCount(Integer userId) {
        return messageRepository.count(new Specification<Message>() {
            @Override
            public Predicate toPredicate(Root<Message> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate predicate = criteriaBuilder.conjunction();
                if (userId != null){
                    predicate.getExpressions().add(criteriaBuilder.equal(root.get("user").get("userId"),userId));
                }
                return predicate;
            }
        });
    }

    /**
     * 添加或者修改消息
     *
     * @param message
     */
    @Override
    public void save(Message message) {
        messageRepository.save(message);
    }

    /**
     * 查看某个用户下所有的消息（未查看的）
     *
     * @param userId
     * @return
     */
    @Override
    public Integer getCountByUserId(Integer userId) {
        return messageRepository.getCountByUserId(userId);
    }

    /**
     * 查看所有消息
     */
    @Override
    @Transactional(rollbackOn = Exception.class)
    public void updateStatus(Integer userId) {
        messageRepository.updateStatus(userId);
    }

    /**
     * 通过id删除消息
     *
     * @param id
     */
    @Override
    public void deleteById(Integer id) {
        messageRepository.deleteById(id);
    }
}
