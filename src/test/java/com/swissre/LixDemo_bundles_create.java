package com.swissre;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.logging.Logger;

public class LixDemo_bundles_create extends ClientCredentialDemo2{

    private static final Logger logger = Logger.getLogger(LixDemo_bundles_create.class.getName());

    public static void main(String[] args) {
        String token = getToken();

        System.out.println("token: "+token);
        System.out.println();
        System.out.println();

        String url= "https://lix.cf-dev.swissre.cn/public/api/v1/cdms/collections/2/bundles";
        LixDemo_bundles_create.doPostTestOne(token, url);
    }

    /**
     * POST---无参测试
     */
    public static void doPostTestOne(String token, String url) {
        // 获得Http客户端(可以理解为:你得先有一个浏览器;注意:实际上HttpClient与浏览器是不一样的)
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        // 创建Get请求
        HttpPost httpPost = new HttpPost(url);

        setRequestConfig(httpPost, token);
        httpPost.setHeader("accept","application/json");
        httpPost.setHeader("content-type","application/json");

        String json = readFileByLines("C:\\srdev\\projects\\nv\\task05-20201221-lix\\test-bundles.txt");
        System.out.println("json: "+json);
        StringEntity entity = new StringEntity(json, "UTF-8");

        // post请求是将参数放在请求体里面传过去的;这里将entity放入post请求体中
        httpPost.setEntity(entity);

        // 响应模型
        CloseableHttpResponse response = null;
        try {
            // 由客户端执行(发送)Get请求
            response = httpClient.execute(httpPost);
            // 从响应模型中获取响应实体
            HttpEntity responseEntity = response.getEntity();
            System.out.println("响应状态为:" + response.getStatusLine());
            if (responseEntity != null) {
                System.out.println("响应内容长度为:" + responseEntity.getContentLength());
                System.out.println("响应内容为:" + EntityUtils.toString(responseEntity));
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // 释放资源
                if (httpClient != null) {
                    httpClient.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}