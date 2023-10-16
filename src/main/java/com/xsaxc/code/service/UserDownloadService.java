package com.xsaxc.code.service;

import com.xsaxc.code.entity.UserDownload;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;

/**
 * @author 师晓峰
 * @version V1.0
 * @date 2023/10/11 13:37
 * @Description:
 *      用户下载 Service 接口
 */
public interface UserDownloadService {

    /**
     * 查询某个用户某个资源的下载次数
     * @param userId
     * @param articleId
     * @return
     */
    public Integer getCountBuUserIdAndArticleId(Integer userId, Integer articleId);

    /**
     * 分页查询某个用户下载的所有资源
     * @param userId
     * @param page   当前页面
     * @param pageSize  每页记录数
     * @param direction  排序规则
     * @param properties  排序字段
     * @return
     */
    public Page<UserDownload> list(Integer userId, Integer page, Integer pageSize, Direction direction,String... properties);


    /**
     * 统计某个用户下载的资源数
     * @param userId
     * @return
     */
    public Long getCount(Integer userId);

    /**
     * 添加或者修改某个用户下载的信息
     * @param userDownload
     */
    public void save(UserDownload userDownload);

    /**
     * 根据id删除资源信息
     *
     * @param article_id
     */
    public void deleteByArticleId(Integer article_id);
}
