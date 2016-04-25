package com.uv.utils;

import net.sf.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by uv2sun on 15/11/26.
 * 用于发送http request测试也可以当发送http request 工具类使用
 */
public class RequestSender {

    private static String sendHttpRequest(String urlString, String params)
            throws IOException {
        System.out.println("send request " + urlString + ", params=" + params);
        // 创建连接
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestMethod("POST");
        connection.setUseCaches(false);
        connection.setRequestProperty("Charset", "UTF-8");
        connection.setInstanceFollowRedirects(true);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.connect();

        // POST请求
        DataOutputStream out = new DataOutputStream(
                connection.getOutputStream());

        /**写入参数，普通key=value&key1=value1方式，入参已按此格式拼接好了*/
        out.writeBytes(params);
        out.flush();
        out.close();
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


    public static void main(String[] args) throws IOException {
        JSONObject data = JSONObject.fromObject("{data_type:'message',data:{content:'中文', msg_type:'sensor_add', sensor_id:1}}");
//        String params = "data_type=" + data.getString("data_type") + "&data=" + URLEncoder.encode(data.getJSONObject("data").toString(), "UTF-8");
        String params = "data_type=" + data.getString("data_type") + "&data=" + URLEncoder.encode(data.getJSONObject("data").toString(), "UTF-8");
//        params = URLEncoder.encode(params, "UTF-8");
        sendHttpRequest("http://127.0.0.1:8080/monitor/wstrigger", params);
    }
}
