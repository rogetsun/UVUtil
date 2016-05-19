package com.uv.utils.http;

import com.uv.utils.http.ssl.CustomizedHostnameVerifier;
import com.uv.utils.http.ssl.MyX509TrustManager;
import net.sf.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import java.io.*;
import java.net.URL;
import java.util.Date;
import java.util.Iterator;

/**
 * Created by uv2sun on 16/5/16.
 */
public class HttpsRequestSender {

    public static final String APPLICATION_FORM = "application/x-www-form-urlencoded;charset=utf-8";
    public static final String APPLICATION_JSON = "application/json;charset=utf-8";
    public static final String POST = "POST";
    public static final String GET = "GET";
    public static final String PUT = "PUT";
    public static final String DELETE = "DELETE";
    public static final String ACCEPT = "application/json;charset=utf-8";


    /**
     * 发送请求基础方法
     *
     * @param urlString   地址,不包含get,delete参数
     * @param data        参数,get和delete情况,拼接到URL后面.put和post放到request.body里
     * @param contentType put和post方式设置,只有这两种才有request content,即request body. APPLICATION_FORM,APPLICATION_JSON两种值.
     * @param method      "PUT","GET","POST","DELETE"
     * @return 响应json字符串, 默认utf-8.因为header里设置了ACCEPT = "application/json;charset=utf-8",如果对方服务器不遵守也没办法.
     * @throws IOException
     */
    public static String sendHttpRequest(String urlString, JSONObject data, String contentType, String method)
            throws Exception {
//        System.out.println("send request " + urlString + ", params=" + params);
        /**
         * 传输参数,get拼接URL,其他方式写入request body
         */
        String params = "";


        /**写入参数，postForm和get格式:key=value&key1=value1方式，postJson,put格式:json字符串*/
        if (HttpRequestSender.APPLICATION_FORM.equals(contentType) || "GET".equals(method) || "DELETE".equals(method)) {
            StringBuffer sb = new StringBuffer();
            if (null != data && !data.isNullObject() && !data.isEmpty()) {
                for (Iterator<String> it = data.keys(); it.hasNext(); ) {
                    String key = it.next();
                    String value = null == data.get(key) ? "" : data.getString(key);
                    if (sb.length() > 0) sb.append("&");
                    sb.append(key + "=" + value);
                }
            }
            params = sb.toString();
        } else if (HttpRequestSender.APPLICATION_JSON.equals(contentType)) {
            params = data.toString();
        }
        /**
         * get方法,直接将参数拼接到URL
         */
        if ("GET".equals(method) || "DELETE".equals(method)) {
            urlString += "?" + params;
            System.out.println(urlString);
        }
        System.setProperty("jsse.enableSNIExtension", "false");

        // 创建连接
        URL url = new URL(urlString);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setHostnameVerifier(new CustomizedHostnameVerifier());
        TrustManager[] tm = {new MyX509TrustManager()};
        SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");

        sslContext.init(null, tm, new java.security.SecureRandom());

        SSLSocketFactory ssf = sslContext.getSocketFactory();
        connection.setSSLSocketFactory(ssf);
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestMethod(method);
        connection.setUseCaches(false);
        connection.setInstanceFollowRedirects(true);
        if (!"GET".equals(method) && !"DELETE".equals(method)) {//非get方式设置
            connection.setRequestProperty("Content-Type", contentType);
        }
        connection.setRequestProperty("Accept", HttpRequestSender.ACCEPT);
        /**
         * 服务器是spring-mvc实现的,默认设置Accept-Charset没用
         */
//        connection.setRequestProperty("Accept-Charset", "utf-8");
        connection.connect();
        //获取响应头Content-Type
//        String s = connection.getHeaderField("Content-Type");
//        System.out.println("response.Content-Type=" + s);
        // POST请求
        /**
         * 非get请求,将参数写入request body
         */
        if (!"GET".equals(method) && !"DELETE".equals(method)) {
            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "utf-8");
            out.write(params);
            out.flush();
            out.close();
        }

        // 读取响应
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                connection.getInputStream(), "UTF-8"));
        String lines;
        StringBuffer sb = new StringBuffer("");
        while ((lines = reader.readLine()) != null) {
            lines = new String(lines.getBytes(), "utf-8");
            sb.append(lines);
        }
        reader.close();
        // 断开连接
        connection.disconnect();
        return sb.toString();
    }

    /**
     * 发送请求,将响应写入os
     *
     * @param urlString
     * @param data
     * @param contentType
     * @param method
     * @param os
     * @throws Exception
     */
    public static void sendHttpRequest(String urlString, JSONObject data, String contentType, String method, OutputStream os)
            throws Exception {
//        System.out.println("send request " + urlString + ", params=" + params);
        /**
         * 传输参数,get拼接URL,其他方式写入request body
         */
        String params = "";


        /**写入参数，postForm和get格式:key=value&key1=value1方式，postJson,put格式:json字符串*/
        if (HttpRequestSender.APPLICATION_FORM.equals(contentType) || "GET".equals(method) || "DELETE".equals(method)) {
            StringBuffer sb = new StringBuffer();
            if (null != data && !data.isNullObject() && !data.isEmpty()) {
                for (Iterator<String> it = data.keys(); it.hasNext(); ) {
                    String key = it.next();
                    String value = null == data.get(key) ? "" : data.getString(key);
                    if (sb.length() > 0) sb.append("&");
                    sb.append(key + "=" + value);
                }
            }
            params = sb.toString();
        } else if (HttpRequestSender.APPLICATION_JSON.equals(contentType)) {
            params = data.toString();
        }
        /**
         * get方法,直接将参数拼接到URL
         */
        if ("GET".equals(method) || "DELETE".equals(method)) {
            urlString += "?" + params;
            System.out.println(urlString);
        }
        System.setProperty("jsse.enableSNIExtension", "false");

        // 创建连接
        URL url = new URL(urlString);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();


        connection.setHostnameVerifier(new CustomizedHostnameVerifier());
        TrustManager[] tm = {new MyX509TrustManager()};
        SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
        sslContext.init(null, tm, new java.security.SecureRandom());
        SSLSocketFactory ssf = sslContext.getSocketFactory();
        connection.setSSLSocketFactory(ssf);


        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestMethod(method);
        connection.setUseCaches(false);
        connection.setInstanceFollowRedirects(true);
        if (!"GET".equals(method) && !"DELETE".equals(method)) {//非get方式设置
            connection.setRequestProperty("Content-Type", contentType);
        }
        connection.setRequestProperty("Accept", HttpRequestSender.ACCEPT);
        /**
         * 服务器是spring-mvc实现的,默认设置Accept-Charset没用
         */
//        connection.setRequestProperty("Accept-Charset", "utf-8");
        connection.connect();
        //获取响应头Content-Type
//        String s = connection.getHeaderField("Content-Type");
//        System.out.println("response.Content-Type=" + s);
        // POST请求
        /**
         * 非get请求,将参数写入request body
         */
        if (!"GET".equals(method) && !"DELETE".equals(method)) {
            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "utf-8");
            out.write(params);
            out.flush();
            out.close();
        }
        InputStream is = connection.getInputStream();
        int d;
        while ((d = is.read()) != -1) {
            System.out.println("read " + d);
            os.write(d);
        }
        is.close();
        connection.disconnect();
    }

    public static String post(String url, JSONObject data) throws Exception {
        return postJSON(url, data);
    }

    public static String postJSON(String url, JSONObject data) throws Exception {
        return sendHttpRequest(url, data, HttpRequestSender.APPLICATION_JSON, HttpRequestSender.POST);
    }

    public static String postForm(String url, JSONObject data) throws Exception {
        return sendHttpRequest(url, data, HttpRequestSender.APPLICATION_FORM, HttpRequestSender.POST);
    }

    public static String delete(String url, JSONObject data) throws Exception {
        return sendHttpRequest(url, data, HttpRequestSender.APPLICATION_JSON, HttpRequestSender.DELETE);
    }

    public static String put(String url, JSONObject data) throws Exception {
        return sendHttpRequest(url, data, HttpRequestSender.APPLICATION_JSON, HttpRequestSender.PUT);
    }


    public static String get(String url, JSONObject data) throws Exception {
        return sendHttpRequest(url, data, null, HttpRequestSender.GET);
    }

    public static void get(String url, JSONObject data, OutputStream os) throws Exception {
        sendHttpRequest(url, data, null, HttpsRequestSender.GET, os);
    }

    public static void main(String[] args) throws Exception {
        JSONObject param = JSONObject.fromObject("{" +
                "        'appid': 'wx782c26e4c19acffb'," +
                "        'fun': 'new'," +
                "        'lang': 'zh_CN'," +
                "        '_': " + new Date().getTime() +
                "    }");
        String r = HttpsRequestSender.get("https://login.weixin.qq.com/jslogin", param);
        System.out.println(r);

    }
}
