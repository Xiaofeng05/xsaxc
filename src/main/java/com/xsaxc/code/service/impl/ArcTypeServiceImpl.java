package com.xsaxc.code.service.impl;

import com.xsaxc.code.entity.ArcType;
import com.xsaxc.code.respository.ArcTypeRepository;
import com.xsaxc.code.run.StartupRunner;
import com.xsaxc.code.service.ArcTypeService;
import com.xsaxc.code.util.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 师晓峰
 * @version V1.0
 * @date 2023/10/1 19:16
 * @Description:
 */
@Service(value = "arcTypeService")
public class ArcTypeServiceImpl implements ArcTypeService {


    @Autowired
    private ArcTypeRepository arcTypeRepository;

    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;

    @Autowired
    private StartupRunner startupRunner;



    @Override
    public List<ArcType> list(Integer page, Integer pageSize, Sort.Direction direction, String... properties) {
        Page<ArcType> arcTypePage = arcTypeRepository.findAll(PageRequest.of(page - 1, pageSize,direction,properties));


        return arcTypePage.getContent();
    }

    @Override
    public List list(Sort.Direction direction, String... properties) {
        if (redisTemplate.hasKey(Const.ALL_ARC_TYPE_NAME)){
            return redisTemplate.opsForList().range(Const.ALL_ARC_TYPE_NAME,0,-1);
        }else{
            List list = arcTypeRepository.findAll(Sort.by(direction,properties));
            if (list != null && list.size() > 0){
                for(int i = 0; i < list.size(); i++){
                    redisTemplate.opsForList().rightPush(Const.ALL_ARC_TYPE_NAME,list.get(i));
                }
            }
            return list;
        }
    }

    @Override
    public Long getCount() {
        return arcTypeRepository.count();
    }

    /**
     * 修改或者新增
     *
     * @param arcType
     */
    @Override
    public void save(ArcType arcType) {
        Boolean flag = false;
        if ((arcType.getArcTypeId() == null)) {
            // 新增
            flag = true;
        }
        arcTypeRepository.save(arcType);
        if (flag){
            // 新增类型
            redisTemplate.opsForList().rightPush(Const.ALL_ARC_TYPE_NAME,arcType);
        }else{
            // 修改类型
            redisTemplate.delete(Const.ALL_ARC_TYPE_NAME);
        }
        startupRunner.loadData();
    }

    /**
     * 删除操作
     * @param id
     */
    @Override
    public void delete(Integer id) {
        arcTypeRepository.deleteById(id);
        // 删除再加载
        redisTemplate.delete(Const.ALL_ARC_TYPE_NAME);
        startupRunner.loadData();
    }

    @Override
    public ArcType getById(Integer id) {
        return arcTypeRepository.getOne(id);
    }




}
