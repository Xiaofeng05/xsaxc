package com.xsaxc.code.util;

import com.xsaxc.code.entity.ArcType;
import com.xsaxc.code.entity.Comment;
import org.apache.commons.lang.time.DateUtils;

import java.util.List;

/**
 * @author 师晓峰
 * @version V1.0
 * @date 2023/10/5 21:54
 * @Description: HTML的工具类
 */
public class HTMLUtil {


    /**
     * 拼接类型列表的编码
     *
     * @param type
     * @param arcTypeList
     * @return
     */
    public static String getArcTypeStr(String type, List<ArcType> arcTypeList) {
        StringBuffer arcTypeCode = new StringBuffer();

        if ("all".equals(type)) {
            arcTypeCode.append("<li class=\"layui-hide-xs layui-this\"><a href=\"/code\">首页<a/></li>");
        } else {
            arcTypeCode.append("<li><a href=\"/code\">首页<a/></li>");
        }

        for (ArcType arcType : arcTypeList) {
            if (type.equals(arcType.getArcTypeId().toString())) {
                arcTypeCode.append("<li class=\"layui-hide-xs layui-this\"><a href=\"http://120.46.82.82:8080/code/article/" +
                        arcType.getArcTypeId() + "/1\">" + arcType.getArcTypeName() + "<a/></li>");

            } else {
                arcTypeCode.append("<li><a href=\"http://120.46.82.82:8080/code/article/" +
                        arcType.getArcTypeId() + "/1\">" + arcType.getArcTypeName() + "<a/></li>");
            }
        }
        return arcTypeCode.toString();
    }


    /**
     * 拼接分页代码
     *
     * @param targetUrl   请求路径
     * @param count       总记录数
     * @param currentPage 当前页
     * @param msg         错误信息
     * @return
     */
    public static String getLayPage(String targetUrl, int count, int currentPage, String msg) {
        // 总页数
        int totalPage = count % Const.PAGE_SIZE == 0 ? count / Const.PAGE_SIZE : (count / Const.PAGE_SIZE) + 1;
        StringBuffer pageCode = new StringBuffer();
        pageCode.append("<div class=\"laypage-main\">");
        if (totalPage > 0) {
            pageCode.append("<a href=\"" + targetUrl + "/1\">首页</a>");

        }
        if (currentPage != 1) {
            pageCode.append("<a style=\"display:inline-block;\" href=\"" + targetUrl + "/" + (currentPage - 1) + "\">上一页</a>");
        }

        for (int i = currentPage - 3; i <= currentPage + 3; i++) {
            if (i < 1 || i > totalPage) {
                continue;
            }
            if (i == currentPage) {
                pageCode.append("<span class=\"laypage-curr\">" + i + "</span>");
            } else {
                pageCode.append("<a href=\"" + targetUrl + "/" + i + "\">" + i + "</a>");
            }
        }
        if (currentPage < totalPage) {
            pageCode.append("<a style=\"display:inline-block;\" href=\"" + targetUrl + "/" + (currentPage + 1) + "\">下一页</a>");
        }

        if (totalPage > 0) {
            pageCode.append("<a href=\"" + targetUrl + "/" + totalPage + "\">尾页</a>");

        } else {
            pageCode.append("<span>" + msg + "</span>");
        }

        pageCode.append("</div>");
        return pageCode.toString();
    }


    /**
     * 搜索分页代码
     *
     * @param targetUrl   请求路径
     * @param totalPage   总记录数
     * @param currentPage 当前页
     * @param msg         错误信息
     * @return
     */
    public static String getLayPageByLucene(String targetUrl, int totalPage, int currentPage, String msg) {
        StringBuffer pageCode = new StringBuffer();
        pageCode.append("<div class=\"laypage-main\">");
        if (totalPage > 0) {
            pageCode.append("<a href=\"" + targetUrl + "\">首页</a>");
        }
        if (currentPage != 1) {
            pageCode.append("<a style=\"display:inline-block;\" href=\"" + targetUrl + "&page=" + (currentPage - 1) + "\">上一页</a>");
        }

        for (int i = currentPage - 3; i <= currentPage + 3; i++) {
            if (i < 1 || i > totalPage) {
                continue;
            }
            if (i == currentPage) {
                pageCode.append("<span class=\"laypage-curr\">" + i + "</span>");
            } else {
                pageCode.append("<a href=\"" + targetUrl + "&page=" + i + "\">" + i + "</a>");
            }
        }
        if (currentPage < totalPage) {
            pageCode.append("<a style=\"display:inline-block;\" href=\"" + targetUrl + "&page=" + (currentPage + 1) + " \">下一页</a>");
        }

        if (totalPage > 0) {
            pageCode.append("<a href=\"" + targetUrl + "\">尾页</a>");
        } else {
            pageCode.append("<span>" + msg + "</span>");
        }

        pageCode.append("</div>");
        return pageCode.toString();
    }

    /**
     * 拼接品论代码
     *
     * @param commentList
     * @return
     */
    public static String getCommentPage(List<Comment> commentList) {
        StringBuffer commentCode = new StringBuffer();

        if (commentList == null || commentList.size() == 0) {
            return commentCode.toString();
        }

        for(Comment comment:commentList){
            commentCode.append("<li class=\"jieda-daan\">\n" +
                    "   <div class=\"detail-about detail-about-reply\">\n" +
                    "       <a class=\"fly-avatar\" href=\"\">\n"   +
                    "           <img src=\"http://120.46.82.82:8080/code/static/img/" + comment.getUser().getHeadPortrait() + "\" alt=\""+comment.getUser().getNickname()+"\" />\n" +
                    "       </a>\n" +
                    "   <div class=\"fly-detail-user\">\n" +
                    "       <a href='' class='fly-link'>\n" +
                    "           <cite>"+comment.getUser().getNickname()+"</cite>\n");
            if(comment.getUser().getVip()){
                commentCode.append("<i class='iconfont icon-renzheng' title='会员认证'></i>\n" +
                        "<i class='layui-badge fly-badge-vip'>VIP" + comment.getUser().getVipGrade() + "</i>\n");
            }
            commentCode.append("       </a>\n");
            //如果作者和评论者是同一个人，显示作者标识
            if(comment.getUser().getUserId().equals(comment.getArticle().getUser().getUserId())){
                commentCode.append("<span>（作者）</span>\n");
            }
            commentCode.append("</div>\n\n");
            commentCode.append("<div class='detail-hits'>\n" +
                    "   <span>"+DateUtil.formatString(comment.getCommentDate(),"yyyy-MM-dd HH:mm")+"</span>\n" +
                    "</div>\n" +
                    "</div>\n");
            commentCode.append("<div class='detail-body jieda-body photos'>\n " +
                    "    <p>" + comment.getContent()+"</p>\n" +
                    "</div>\n"+
                    "</li>");
        }

        return commentCode.toString();
    }
}
