package com.xsaxc.code.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author 师晓峰
 * @version V1.0
 * @date 2023/10/7 22:57
 * @Description:
 *      日期工具类
 */
public class DateUtil {

    /**
     * 字符串转日期对象
     * @param str
     * @param format
     * @return
     */
    public static Date formatString(String str,String format) throws ParseException {
        if (StringUtil.isEmpty(str)){
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.parse(str);
    }

    /**
     * 日期对象转字符串对象
     * @param date
     * @param format
     * @return
     */
    public static String formatString(Date date,String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        if (date != null){
            return sdf.format(date);
        }
        return "";
    }

    public static String  getCurrentDate(){
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        // 可以再加一个随机数
        return sdf.format(date);
    }
}
