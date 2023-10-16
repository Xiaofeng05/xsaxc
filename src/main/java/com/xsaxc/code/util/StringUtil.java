package com.xsaxc.code.util;

import java.util.Random;

/**
 * @author 师晓峰
 * @version V1.0
 * @date 2023/9/29 21:21
 * @Description: 字符串操作工具类
 */
public class StringUtil {

    /**
     * 判断字符串是否为空
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        if (str == null || str.trim().equals("")) {
            return true;
        }
        return false;
    }


    public static boolean isNotEmpty(String str) {
        if (str != null && !str.trim().equals("")) {
            return true;
        }
        return false;
    }

    /**
     * 生成6位数字验证码
     *
     * @return
     */
    public static String genSixRandom() {
        Random random = new Random();
        String result = "";
        for (int i = 0; i < 6; i++) {
            result += random.nextInt(10);
        }
        return result;
    }


    /**
     * 去除HTML标签
     *
     * @param content
     */
    public static String stripHtml(String content) {
        // <p> 段落替换
        content = content.replace("<p.*?>", "\r\n");
        // <br></br> 替换成换行
        content.replaceAll("<br\\s*?>", "\r\n");
        // 去掉去掉其他<>之间的东西
        content = content.replaceAll("\\<.*?>", "");
        // 去掉所有空格
        content = content.replaceAll(" ", "");
        return content;
    }


    /**
     * 转义大于小于符号
     *
     * @param content
     * @return
     */
    public static String exc(String content) {
        return content.replace("<", "&lt;")
                      .replace(">", "&gt;");
    }


    public static void main(String[] args) {
        System.out.println(genSixRandom());
    }

}
