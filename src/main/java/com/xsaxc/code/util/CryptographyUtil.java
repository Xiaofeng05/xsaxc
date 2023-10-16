package com.xsaxc.code.util;

import org.apache.shiro.crypto.hash.Md5Hash;

/**
 * @author 师晓峰
 * @version V1.0
 * @date 2023/9/29 17:17
 * @Description:
 *      加密工具
 */
public class CryptographyUtil {
    public final static String SALT = "code-xsaxc";

    /**
     * md5 加密
     * @param str
     * @param salt
     * @return
     */
    public static String md5(String str, String salt) {
        return new Md5Hash(str, salt).toString();
    }

    public static  void main(String[] args){
        String password = "123456";
        System.out.println(CryptographyUtil.md5(password,SALT));
    }
}
