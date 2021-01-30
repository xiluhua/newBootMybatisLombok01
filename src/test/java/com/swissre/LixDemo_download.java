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

import java.io.IOException;
import java.util.logging.Logger;

public class LixDemo_download extends ClientCredentialDemo2{

    private static final Logger logger = Logger.getLogger(ClientCredentialDemo2.class.getName());

    public static void main(String[] args) {
        String token = getToken();

        System.out.println("token: "+token);
        System.out.println();
        System.out.println();
        try {
//            String documentId = "53fb56a6-6d43-426e-a1cc-aff7befb76d2";
            String documentId = "76ec880f-93a0-4708-8185-87abfcf38549";//"53fb56a6-6d43-426e-a1cc-aff7befb76d2";
            String url = "https://lix.cf-dev.swissre.cn/public/api/v1/cdms/collections/2/documents/"+documentId+"/download";
            byte[] bytes = LixDemo_download.doGetTestOne(token, url);
            String path = getPath();

            write(path, bytes);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * GET---无参测试
     */
    public static byte[] doGetTestOne(String token, String url) {
        byte[] buffer = null;
        // 获得Http客户端(可以理解为:你得先有一个浏览器;注意:实际上HttpClient与浏览器是不一样的)
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        // 创建Get请求
        HttpGet httpGet = new HttpGet(url);

        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setSocketTimeout(50000)
                .setConnectTimeout(50000)
                .setConnectionRequestTimeout(50000)
                .setStaleConnectionCheckEnabled(true)
                .build();

        RequestConfig requestConfig = RequestConfig.copy(defaultRequestConfig)
                .setProxy(new HttpHost("gate-zrh-os.swissre.com", 8080))
                .build();

        httpGet.setConfig(requestConfig);
        httpGet.setHeader("Authorization","Bearer "+token);
        httpGet.setHeader("accept","application/octet-stream");
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
                buffer = read(responseEntity.getContent());
//                System.out.println("响应内容为:" + EntityUtils.toString(responseEntity));
            }
            System.out.println();
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
        return buffer;
    }

}