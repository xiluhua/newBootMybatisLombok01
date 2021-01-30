package com.swissre;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.logging.Logger;

public class LixDemo_bundles_query extends ClientCredentialDemo2{

    private static final Logger logger = Logger.getLogger(LixDemo_bundles_query.class.getName());

    public static void main(String[] args) {
        String token = getToken();

        System.out.println("token: "+token);
        System.out.println();
        System.out.println();

        String url = "https://lix.cf-dev.swissre.cn/public/api/v1/cdms/collections/2/bundles";
        LixDemo_bundles_query.doGetTestOne(token, url);
    }

    /**
     * GET---无参测试
     */
    public static void doGetTestOne(String token, String url) {
        // 获得Http客户端(可以理解为:你得先有一个浏览器;注意:实际上HttpClient与浏览器是不一样的)
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        // 创建Get请求
        HttpGet httpGet = new HttpGet(url);

        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setSocketTimeout(5000)
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .setStaleConnectionCheckEnabled(true)
                .build();

        RequestConfig requestConfig = RequestConfig.copy(defaultRequestConfig)
                .setProxy(new HttpHost("gate-zrh-os.swissre.com", 8080))
                .build();

        httpGet.setConfig(requestConfig);
        httpGet.setHeader("Authorization","Bearer "+token);
        httpGet.setHeader("accept","application/json");
        httpGet.setHeader("content-type","application/json");
        // 响应模型
        CloseableHttpResponse response = null;
        try {
            // 由客户端执行(发送)Get请求
            response = httpClient.execute(httpGet);
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