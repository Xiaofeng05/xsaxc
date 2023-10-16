package com.xsaxc.code.controller;

import com.xsaxc.code.entity.Comment;
import com.xsaxc.code.entity.User;
import com.xsaxc.code.service.CommentService;
import com.xsaxc.code.util.Const;
import com.xsaxc.code.util.HTMLUtil;
import com.xsaxc.code.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 师晓峰
 * @version V1.0
 * @date 2023/10/11 20:10
 * @Description: 评论控制器
 */
@Controller()
@RequestMapping("/comment")
public class CommentController {


    @Autowired
    private CommentService commentService;


    /**
     * 添加评论信息
     *
     * @param comment
     * @param session
     * @return
     */
    @RequestMapping("/add")
    @ResponseBody
    public Map<String, Object> add(Comment comment, HttpSession session) {
        Map<String, Object> map = new HashMap<String, Object>();
        comment.setContent(StringUtil.exc(comment.getContent()));
        comment.setCommentDate(new Date());
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        comment.setUser(currentUser);
        commentService.save(comment);
        map.put("success", true);
        return map;
    }


    @RequestMapping("/list")
    @ResponseBody
    public Map<String, Object> list(Comment comment, @RequestParam(value = "page",required = false) Integer page) {
        Map<String, Object> map = new HashMap<String, Object>();
        comment.setState(1);
        Page<Comment> commentPage = commentService.list(comment, page, 5, Direction.DESC, "commentDate");


        // 获取评论的HTML的代码
        String commentPageCode = HTMLUtil.getCommentPage(commentPage.getContent());
        map.put("data", commentPageCode);

        map.put("total",commentPage.getTotalPages());
        return map;
    }


}
