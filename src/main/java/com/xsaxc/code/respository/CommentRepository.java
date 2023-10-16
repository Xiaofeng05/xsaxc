package com.xsaxc.code.respository;

import com.xsaxc.code.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * @author 师晓峰
 * @version V1.0
 * @date 2023/10/11 17:50
 * @Description: 评论Repository 接口
 */
public interface CommentRepository extends JpaRepository<Comment, Integer>, JpaSpecificationExecutor<Comment> {


    /**
     * 删除指定资源下的所有评论
     * @param articleId
     */
    @Query(value = "delete  from comment where article_id = ?1",nativeQuery = true)
    @Modifying
    public void deleteByArticleId(Integer articleId);

}
