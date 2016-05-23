package com.uv.utils.http;

import com.uv.utils.UVLog;
import net.sf.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by uv2sun on 15/11/26.
 * 用于发送http request测试也可以当发送http request 工具类使用
 */
public class HttpRequestSender {

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
    public static String sendHttpRequest(String urlString, JSONObject data, String contentType, String method, Map<String, String> cookie)
            throws IOException {
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
        // 创建连接
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
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
         * cookie设置
         */
        if (null != cookie) {
            StringBuffer cookieString = new StringBuffer();
            for (Map.Entry e : cookie.entrySet()) {
                cookieString.append(e.getKey() + "=" + e.getValue() + ";");
            }
            connection.setRequestProperty("Cookie", cookieString.toString());
        }
        /**
         * 服务器是spring-mvc实现的,默认设置Accept-Charset没用
         */
//        connection.setRequestProperty("Accept-Charset", "utf-8");
        connection.connect();
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

        UVLog.debug("response.header.Content-Type=" + connection.getHeaderField("Content-Type"));
        //遍历响应头
        if (cookie != null) {
            List<String> l = connection.getHeaderFields().get("Set-Cookie");
            for (String s : l) {
                HttpCookie hc = HttpCookie.parse(s).get(0);
                cookie.put(hc.getName(), hc.getValue());
            }
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

    public static String post(String url, JSONObject data) throws IOException {
        return postJSON(url, data);
    }

    public static String postJSON(String url, JSONObject data) throws IOException {
        return sendHttpRequest(url, data, HttpRequestSender.APPLICATION_JSON, HttpRequestSender.POST, null);
    }

    public static String postForm(String url, JSONObject data) throws IOException {
        return sendHttpRequest(url, data, HttpRequestSender.APPLICATION_FORM, HttpRequestSender.POST, null);
    }

    public static String delete(String url, JSONObject data) throws IOException {
        return sendHttpRequest(url, data, HttpRequestSender.APPLICATION_JSON, HttpRequestSender.DELETE, null);
    }

    public static String put(String url, JSONObject data) throws IOException {
        return sendHttpRequest(url, data, HttpRequestSender.APPLICATION_JSON, HttpRequestSender.PUT, null);
    }

    public static String get(String url, JSONObject data) throws IOException {
        return sendHttpRequest(url, data, null, HttpRequestSender.GET, null);
    }

    /**
     * 带cookie的一套方法
     */
    public static String post(String url, JSONObject data, Map cookie) throws IOException {
        return postJSON(url, data, cookie);
    }

    public static String postJSON(String url, JSONObject data, Map cookie) throws IOException {
        return sendHttpRequest(url, data, HttpRequestSender.APPLICATION_JSON, HttpRequestSender.POST, cookie);
    }

    public static String postForm(String url, JSONObject data, Map cookie) throws IOException {
        return sendHttpRequest(url, data, HttpRequestSender.APPLICATION_FORM, HttpRequestSender.POST, cookie);
    }

    public static String delete(String url, JSONObject data, Map cookie) throws IOException {
        return sendHttpRequest(url, data, HttpRequestSender.APPLICATION_JSON, HttpRequestSender.DELETE, cookie);
    }

    public static String put(String url, JSONObject data, Map cookie) throws IOException {
        return sendHttpRequest(url, data, HttpRequestSender.APPLICATION_JSON, HttpRequestSender.PUT, cookie);
    }


    public static String get(String url, JSONObject data, Map cookie) throws IOException {
        return sendHttpRequest(url, data, null, HttpRequestSender.GET, cookie);
    }


    public static void main(String[] args) throws IOException {
        JSONObject data = JSONObject.fromObject("{data_type:'message',data:{content:'中文', msg_type:'sensor_add', sensor_id:1}}");
//        String ret = HttpRequestSender.get("http://127.0.0.1:8080/credit/test", data, JSONObject.fromObject("{sessionid:'fdsakiewjkfdsjkl',id:123321}"));
        JSONObject cookie = new JSONObject();
        String ret = HttpRequestSender.get("http://127.0.0.1:8080/credit/test", data, cookie);
        System.out.println(cookie);
        System.out.println(ret);
    }
}
