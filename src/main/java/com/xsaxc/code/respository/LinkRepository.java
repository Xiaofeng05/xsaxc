package com.xsaxc.code.respository;

import com.xsaxc.code.entity.Link;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author 师晓峰
 * @version V1.0
 * @date 2023/10/13 18:16
 * @Description:
 *      友情链接 Repository
 */
public interface LinkRepository extends JpaRepository<Link,Integer> , JpaSpecificationExecutor<Link> {
}
