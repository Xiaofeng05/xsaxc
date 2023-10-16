package com.xsaxc.code.controller.admin;

import com.xsaxc.code.entity.ArcType;
import com.xsaxc.code.run.StartupRunner;
import com.xsaxc.code.service.ArcTypeService;
import com.xsaxc.code.util.Const;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 师晓峰
 * @version V1.0
 * @date 2023/10/4 22:54
 * @Description: 管理员资源类型 控制器
 */
@RestController
@RequestMapping("/admin/arcType")
public class ArcTypeAdminController {

    @Autowired
    private ArcTypeService arcTypeService;

    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;

    @Autowired
    private StartupRunner startupRunner;


    /**
     * 查询所有资源类型(带分页）
     *
     * @param page     当前页
     * @param pageSize 每页的记录数
     * @return
     */
    @RequestMapping("/list")
    public Map<String, Object> list(@RequestParam(value = "page", required = false) Integer page,
                                    @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        Map<String, Object> map = new HashMap<String, Object>();
        int count = arcTypeService.getCount().intValue();
        if (page == null && pageSize == null) {
            page = 1;
            pageSize = count > 0 ? count : 5;
        }
        List<ArcType> arcTypeList = arcTypeService.list(page, pageSize, Direction.ASC, "sort");

        map.put("data", arcTypeList);
        map.put("total", count);
        map.put("errorNo", 0);
        return map;
    }

    /**
     * 根据id查询资源类型信息
     *
     * @param arcTypeId
     * @return
     */
    @RequestMapping("/findById")
    @RequiresPermissions("根据id查询资源类型实体")
    public Map<String, Object> getById(Integer arcTypeId) {
        Map<String, Object> map = new HashMap<String, Object>();
        ArcType arcType = arcTypeService.getById(arcTypeId);
        map.put("data", arcType);
        map.put("errorNo", 0);
        return map;
    }

    /**
     * 根据id查询资源类型信息
     *
     * @param arcType
     * @return
     */
    @RequestMapping("/save")
    @RequiresPermissions("添加或者修改资源类型信息")
    public Map<String, Object> save(ArcType arcType) {
        Map<String, Object> map = new HashMap<String, Object>();

        arcTypeService.save(arcType);
        map.put("data", arcType);
        map.put("errorNo", 0);
        return map;
    }


    /**
     * 批量删除资源类型信息
     *
     * @param ids
     * @return
     */
    @RequestMapping("/delete")
    @RequiresPermissions("删除资源类型信息")
    public Map<String, Object> delete(@RequestParam(value = "arcTypeId") String ids) {
        Map<String, Object> map = new HashMap<String, Object>();
        String[] idsStr = ids.split(",");
        for (int i = 0; i < idsStr.length; i++) {
            arcTypeService.delete(Integer.parseInt(idsStr[i]));
        }
        redisTemplate.delete(Const.ALL_ARC_TYPE_NAME);
        startupRunner.loadData();
        map.put("errorNo", 0);
        return map;
    }

}
