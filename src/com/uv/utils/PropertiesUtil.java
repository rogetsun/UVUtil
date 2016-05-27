package com.uv.utils;

import net.sf.json.JSONObject;

import java.io.*;
import java.util.Properties;

/**
 * Created by uv2sun on 16/5/27.
 */
public class PropertiesUtil {
    public static JSONObject loadProperties(String propertiesFile) throws IOException {
        Properties p = new Properties();
        try {
            //读取jar包外的和jar包同级的位置
            InputStream in = new BufferedInputStream(new FileInputStream(propertiesFile));
            p.load(in);
        } catch (IOException e) {
            e.printStackTrace();
            try {
                //读取jar包内的位置
                InputStream in = ClassLoader.getSystemResourceAsStream(propertiesFile);
                p.load(in);
            } catch (IOException e1) {
                e1.printStackTrace();
                throw e1;
            }
        }
        return JSONObject.fromObject(p);
    }
}
