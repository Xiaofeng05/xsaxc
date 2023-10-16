package com.xsaxc.code.respository;

import com.xsaxc.code.entity.UserDownload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * @author 师晓峰
 * @version V1.0
 * @date 2023/10/11 13:32
 * @Description:
 *      用户下载 Repository 接口
 */

public interface UserDownloadRepository extends JpaRepository<UserDownload,Integer> , JpaSpecificationExecutor<UserDownload> {

    /**
     * 查询某个用户某个资源的下载次数
     * @param userId
     * @param articleId
     * @return
     */
    @Query(value = "SELECT count(*) FROM user_download where user_id = ?1 and article_id = ?2",nativeQuery = true)
    public Integer getCountBuUserIdAndArticleId(Integer userId, Integer articleId);

    /**
     * 根据资源id 删除用户资源下载记录
     * @param article_id
     */
    @Modifying
    @Query(value ="delete  from user_download where article_id= ?1",nativeQuery = true)
    void deleteByArticleId(Integer article_id);
}
