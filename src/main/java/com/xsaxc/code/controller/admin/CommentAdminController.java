package com.xsaxc.code.controller.admin;

import com.xsaxc.code.entity.Comment;
import com.xsaxc.code.service.CommentService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 师晓峰
 * @version V1.0
 * @date 2023/10/11 21:19
 * @Description:
 */
@RestController
@RequestMapping("/admin/comment")
public class CommentAdminController {

    @Autowired
    private CommentService commentService;

    @RequestMapping("/list")
    @RequiresPermissions(value = "分页查询评论信息")
    public Map<String, Object> list(Comment s_Comment, @RequestParam(value = "page", required = false) Integer page,
                                    @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        Map<String, Object> map = new HashMap<String, Object>();

        Page<Comment> commentPage = commentService.list(s_Comment, page, pageSize, Sort.Direction.DESC, "commentDate");
        map.put("data", commentPage.getContent());
        long total = commentService.getTotal(s_Comment);
        map.put("total", total);
        map.put("errorNo", 0);
        return map;
    }

    @RequestMapping("/updateState")
    @RequiresPermissions(value = "修改该评论状态")
    public Map<String, Object> updateState(Integer commentId, boolean state) {
        Map<String, Object> map = new HashMap<String, Object>();
        Comment comment = commentService.getById(commentId);
        if (state){
            // 审核通过
            comment.setState(1);
        }else{
            // 审核驳回
            comment.setState(2);
        }
        commentService.save(comment);
        map.put("success", true);
        return map;
    }

    @RequestMapping("/delete")
    @RequiresPermissions(value = "删除评论信息")
    public Map<String, Object> delete(@RequestParam(value = "commentId") String ids) {
        Map<String, Object> map = new HashMap<String, Object>();
        String[] idStr = ids.split(",");
        for (int i = 0; i < idStr.length; i++) {
            commentService.delete(Integer.parseInt(idStr[i]));
        }
        map.put("errorNo", 0);
        return map;
    }


}
