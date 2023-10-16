package com.xsaxc.code.respository;

import com.xsaxc.code.entity.ArcType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author 师晓峰
 * @version V1.0
 * @date 2023/10/1 0:54
 * @Description:
 *      资源类型ArcTypeRepository 接口
 */
public interface ArcTypeRepository extends JpaRepository<ArcType,Integer>, JpaSpecificationExecutor<ArcType> {
}
