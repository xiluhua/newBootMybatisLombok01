package com.swissre;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.ParseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.logging.Logger;

public class LixDemo_upload extends ClientCredentialDemo2{

    private static final Logger logger = Logger.getLogger(ClientCredentialDemo2.class.getName());

    public static void main(String[] args) {
        String token = getToken();

        System.out.println("token: "+token);
        System.out.println();
        System.out.println();
        try {
//            String url = "http://localhost:12345/file2";
            String url = "https://lix.cf-dev.swissre.cn/public/api/v1/cdms/collections/2/documents";
            String filePath = "C:\\srdev\\projects\\nv\\task05-20201221-lix\\test3.csv";//"WebStorm2018.3.5.zip";
            LixDemo_upload.upload(filePath, url, token);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * GET---无参测试
     */
    public static void upload(String filePath, String url, String token) {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse response = null;
        try {
            MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
            // 第一个文件
            String filesKey = "file";
            File file1 = new File(filePath);
//            multipartEntityBuilder.addBinaryBody(filesKey, file1);
            // 第二个文件(多个文件的话，使用同一个key就行，后端用数组或集合进行接收即可)
//            File file2 = new File(filePath);
            // 防止服务端收到的文件名乱码。 我们这里可以先将文件名URLEncode，然后服务端拿到文件名时在URLDecode。就能避免乱码问题。
            // 文件名其实是放在请求头的Content-Disposition里面进行传输的，如其值为form-data; name="files"; filename="头像.jpg"
            multipartEntityBuilder.addBinaryBody(filesKey, file1, ContentType.DEFAULT_BINARY, URLEncoder.encode(file1.getName(), "utf-8"));
            // 其它参数(注:自定义contentType，设置UTF-8是为了防止服务端拿到的参数出现乱码)
//            ContentType contentType = ContentType.create("text/plain", Charset.forName("UTF-8"));
//            multipartEntityBuilder.addTextBody("name", "邓沙利文", contentType);
//            multipartEntityBuilder.addTextBody("age", "25", contentType);
            String json = readFileByLines("C:\\srdev\\projects\\nv\\task05-20201221-lix\\test-upload.txt");
            String fileName = file1.getName();
            long size = file1.length();
            json = json.replace("#fileName",fileName);
            json = json.replace("#size", size+"");
            json = json.replace("#bundleId", UUID.randomUUID().toString());
            System.out.println("json: " + json);

            multipartEntityBuilder.addTextBody("document", json, ContentType.APPLICATION_JSON);
            HttpEntity httpEntity = multipartEntityBuilder.build();
            httpPost.setEntity(httpEntity);

//            StringEntity entity = new StringEntity(json, "UTF-8");
//            multipartEntityBuilder.addTextBody("document", json, ContentType.APPLICATION_JSON);

            RequestConfig defaultRequestConfig = RequestConfig.custom()
                    .setSocketTimeout(5000)
                    .setConnectTimeout(5000)
                    .setConnectionRequestTimeout(5000)
                    .setStaleConnectionCheckEnabled(true)
                    .build();

            RequestConfig requestConfig = RequestConfig.copy(defaultRequestConfig)
                    .setProxy(new HttpHost("gate-zrh-os.swissre.com", 8080))
                    .build();

            httpPost.setConfig(requestConfig);
            httpPost.setHeader("Authorization", "Bearer " + token);
            httpPost.setHeader("accept", "application/json");
//            httpPost.setHeader("content-type", "multipart/form-data");

            response = httpClient.execute(httpPost);
            HttpEntity responseEntity = response.getEntity();
            System.out.println("HTTPS响应状态为:" + response.getStatusLine());
            if (responseEntity != null) {
                System.out.println("HTTPS响应内容长度为:" + responseEntity.getContentLength());
                // 主动设置编码，来防止响应乱码
                String responseStr = EntityUtils.toString(responseEntity, StandardCharsets.UTF_8);
                System.out.println("HTTPS响应内容为:" + responseStr);
            }
        } catch (ParseException | IOException e) {
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