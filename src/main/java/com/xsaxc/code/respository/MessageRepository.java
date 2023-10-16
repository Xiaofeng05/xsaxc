package com.xsaxc.code.respository;

import com.xsaxc.code.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * @author 师晓峰
 * @version V1.0
 * @date 2023/10/14 17:34
 * @Description:
 *  This class 消息 Repository
 */
public interface MessageRepository extends JpaRepository<Message,Integer>, JpaSpecificationExecutor<Message> {


    /**
     * 查看某个用户下所有的消息（未查看的）
     * @param userId
     * @return
     */
    @Query(value = "select * from message where is_see = false and user_id = ?1",nativeQuery = true)
    public Integer getCountByUserId(Integer userId);


    /**
     * 查看所有消息
     * @param userId
     */
    @Query(value = "update message set is_see = true where user_id = ?1",nativeQuery =true)
    @Modifying
    public void updateStatus(Integer userId);

}
