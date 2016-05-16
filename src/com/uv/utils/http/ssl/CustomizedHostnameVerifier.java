package com.uv.utils.http.ssl;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * Created by uv2sun on 16/5/16.
 * 个人实现https站点校验认为安全
 */
public class CustomizedHostnameVerifier implements HostnameVerifier {

    @Override
    public boolean verify(String s, SSLSession sslSession) {
        return true;
    }
}
