package com.swissre;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientCredentialDemo2 {
    private static final Logger logger = Logger.getLogger(ClientCredentialDemo2.class.getName());
    private static final Pattern pat = Pattern.compile(".*\"access_token\"\\s*:\\s*\"([^\"]+)\".*");

    public static String getToken() {
        String tokenUrl = "https://login.microsoftonline.com/45597f60-6e37-4be7-acfb-4c9e23b261ea/oauth2/v2.0/token";
        String clientId = "b91f7a91-6d81-4364-80c1-fb202615378a";
        String clientSecret = "6K~.3t7D2-jqlV_eoqai11x.wK9H-1AIN-";
        String scope = "80fc51d3-9a91-44be-8a59-3aa3a135db5d/.default";
        String token = ClientCredentialDemo2.getClientCredentials(tokenUrl, clientId, clientSecret, scope);
        System.out.println("token2: "+token);
        return token;
    }

    public static String getClientCredentials(String tokenUrl, String clientId, String clientSecret, String scope) {
        logger.log(Level.INFO, "Logging in on auth endpoint {0} using clientId {1}", new Object[] { tokenUrl, clientId });;

//        String auth = clientId + ":" + clientSecret;
//        String authentication = Base64.getEncoder().encodeToString(auth.getBytes());
        String content = "grant_type=client_credentials";
        if(scope != null) {
            content += "&scope=" + scope;
            content += "&client_id=" + clientId;
            content += "&client_secret=" + clientSecret;
        }
        BufferedReader reader = null;
        HttpsURLConnection connection = null;
        String returnValue = "";
        String response = "";
        try {

            //创建代理服务器 http://gate-zrh-os.swissre.com:8080
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("gate-zrh-os.swissre.com", 8080));
            //设置代理的用户名密码
//            Authenticator.setDefault(new MyAuth("用户名", "密码"));
            // 设定连接的相关参数
            URL url = new URL(tokenUrl);
            connection = (HttpsURLConnection) url.openConnection(proxy);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
//            connection.setRequestProperty("Authorization", "Basic " + authentication);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Cache-Control", "no-cache");
            PrintStream os = new PrintStream(connection.getOutputStream());
            os.print(content);
            os.close();
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = null;
            StringWriter out = new StringWriter(connection.getContentLength() > 0 ? connection.getContentLength() : 2048);
            while ((line = reader.readLine()) != null) {
                out.append(line);
            }
            response = out.toString();
            Matcher matcher = pat.matcher(response);
            if (matcher.matches() && matcher.groupCount() > 0) {
                returnValue = matcher.group(1);
            }
        } catch (Exception e) {
            System.out.println("Error : " + e.getMessage() + " - message: " + response);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
            connection.disconnect();
        }
        return returnValue;
    }

    static class MyAuth extends Authenticator
    {
        private String user;
        private String pass;

        public MyAuth(String user, String pass)
        {
            this.user  = user;
            this.pass = pass;
        }

        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return  new PasswordAuthentication(user, pass.toCharArray());
        }
    }

    protected static void setRequestConfig(HttpPost httpPost, String token){
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
        httpPost.setHeader("Authorization","Bearer "+token);
    }

    protected static String getPath() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String now = dtf.format(LocalDateTime.now());
        String dir = "/lix/"+now;
        return dir+"/test2.csv";
    }

    /**
     * 使用ByteArrayOutputStream将流变成字节
     * @param in InputStream,输入流
     * @return 字节数组
     * @throws Exception
     */
    public static byte[] read(InputStream in) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            if (in != null) {
                byte[] buffer = new byte[1024];
                int length = 0;
                while ((length = in.read(buffer)) != -1) {
                    out.write(buffer, 0, length);
                }
                out.close();
                in.close();
                return out.toByteArray();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in!=null) {
                    in.close();
                }
                if (out!=null) {
                    out.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }

        return null;
    }

    /**
     * 把byte转文件
     * @param path
     * @param b
     * @throws Exception
     */
    public static void write(String path,byte[] b) throws Exception {
        File file = new File(path);
        if(file.exists()){
            file.delete();
        }
        FileOutputStream outStream;
        outStream = new FileOutputStream(file);
        outStream.write(b);
        outStream.close();
    }

    /**
     * 以行为单位读取文件，常用于读面向行的格式化文件
     */
    public static String readFileByLines(String fileName) {
        StringBuffer buffer = new StringBuffer();
        File file = new File(fileName);
        BufferedReader reader = null;
        try {
            System.out.println("以行为单位读取文件内容，一次读一整行：");
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                // 显示行号
                System.out.println("line " + line + ": " + tempString);
                line++;
                buffer.append(tempString);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return buffer.toString();
    }

    public static void main(String[] args) {
        String tokenUrl = "https://login.microsoftonline.com/45597f60-6e37-4be7-acfb-4c9e23b261ea/oauth2/v2.0/token";
        String clientId = "b91f7a91-6d81-4364-80c1-fb202615378a";
        String clientSecret = "6K~.3t7D2-jqlV_eoqai11x.wK9H-1AIN-";
        String scope = "80fc51d3-9a91-44be-8a59-3aa3a135db5d/.default";
        String token = ClientCredentialDemo2.getClientCredentials(tokenUrl, clientId, clientSecret, scope);
        System.out.println("token2: "+token);
    }
}
