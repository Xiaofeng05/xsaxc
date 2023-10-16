package com.xsaxc.code.controller.admin;

import com.xsaxc.code.entity.Link;
import com.xsaxc.code.run.StartupRunner;
import com.xsaxc.code.service.LinkService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 师晓峰
 * @version V1.0
 * @date 2023/10/13 18:39
 * @Description: 管理员友情链接控制器
 */
@RestController
@RequestMapping("/admin/link")
public class LinkAdminController {

    @Autowired
    private LinkService linkService;

    @Autowired
    private StartupRunner startupRunner;


    /**
     * 分页查询友情链接信息
     *
     * @param page
     * @param pageSize
     * @return
     */
    @RequestMapping("/list")
    @RequiresPermissions(value = {"分页查询友情链接信息"})
    public Map<String, Object> list(@RequestParam(value = "page", required = false) Integer page,
                                    @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("data", linkService.list(page, pageSize, Direction.ASC, "sort"));
        map.put("total", linkService.getTotal());
        map.put("errorNo", 0);
        return map;
    }

    /**
     * 根据id查询友情链接信息
     *
     * @param linkId
     * @return
     */
    @RequestMapping("/findById")
    @RequiresPermissions(value = {"根据id查询友情链接信息"})
    public Map<String, Object> findById(Integer linkId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("data", linkService.getById(linkId));
        map.put("errorNo", 0);
        return map;
    }

    /**
     * 添加或者修改友情链接信息
     *
     * @param link
     * @return
     */
    @RequestMapping("/save")
    @RequiresPermissions(value = {"添加或者修改友情链接信息"})
    public Map<String, Object> save(Link link) {
        Map<String, Object> map = new HashMap<String, Object>();
        if(link.getLinkId() == null){
            linkService.save(link);
        }else{
            Link oldLink = linkService.getById(link.getLinkId());
            oldLink.setLinkName(link.getLinkName());
            oldLink.setLinkUrl(link.getLinkUrl());
            oldLink.setLinkEmail(link.getLinkEmail());
            oldLink.setSort(link.getSort());
            linkService.save(oldLink);
        }

        startupRunner.loadData();
        map.put("errorNo", 0);
        return map;
    }

    /**
     * 批量删除友情链接信息
     *
     * @param ids
     * @return
     */
    @RequestMapping("/delete")
    @RequiresPermissions(value = {"批量删除友情链接信息"})
    public Map<String, Object> delete(@RequestParam(value = "linkId") String ids) {
        Map<String, Object> map = new HashMap<String, Object>();
        String[] idStr = ids.split(",");
        for (int i = 0; i < idStr.length; i++) {
            linkService.delete(Integer.parseInt(idStr[i]));
        }
        startupRunner.loadData();
        map.put("errorNo", 0);
        return map;
    }

}
