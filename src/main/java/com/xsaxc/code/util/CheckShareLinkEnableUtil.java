package com.xsaxc.code.util;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * @author 师晓峰
 * @version V1.0
 * @date 2023/10/3 16:02
 * @Description:
 *      检测分享链接是否有效
 */
public class CheckShareLinkEnableUtil {

    static CloseableHttpClient httpClient = HttpClients.createDefault();


    /**
     * 检查链接是否有效
     * @param link
     * @return
     */
    public static boolean check(String link) throws IOException {
        HttpGet httpGet = new HttpGet(link);
        // 设置请求消息头
        httpGet.setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Safari/537.36");
        // 请求
        CloseableHttpResponse  response =  httpClient.execute(httpGet);
        // 获取返回实体
        HttpEntity entity = response.getEntity();
        String result = EntityUtils.toString(entity,"UTF-8");
        if (result.contains("请输入提取码，不区分大小写")|| result.contains("永久有效")){
            return true;
        }else {
            return false;
        }
    }


    public static void main(String[] args) throws IOException {
        String link = "https://pan.baidu.com/s/1bCagv4BWSxy0P95cYAgaqA";
        System.out.println(check(link));
    }
}
