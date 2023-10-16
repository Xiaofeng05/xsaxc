package com.xsaxc.code.service;

import com.xsaxc.code.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;


import java.util.List;

/**
 * @author 师晓峰
 * @version V1.0
 * @date 2023/10/14 17:39
 * @Description: 消息Service 接口
 */
public interface MessageService {


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
    public Page<Message> list(Integer userId, Integer page, Integer pageSize, Sort.Direction direction, String... properties);


    /**
     * 根据用户id获取总记录数
     * @param userId
     * @return
     */
    public Long getCount(Integer userId);

    /**
     * 添加或者修改消息
     * @param message
     */
    public void save(Message message);

    /**
     * 查看某个用户下所有的消息（未查看的）
     *
     * @param userId
     * @return
     */
    public Integer getCountByUserId(Integer userId);

    /**
     * 查看所有消息
     */
    public void updateStatus(Integer userId);

    /**
     * 通过id删除消息
     * @param id
     */
    public void deleteById(Integer id);

}
