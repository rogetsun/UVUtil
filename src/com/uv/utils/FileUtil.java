package com.uv.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;

/**
 * Created by uv2sun on 2016/11/9.
 */
public class FileUtil {
    private static final Log log = LogFactory.getLog(FileUtil.class);

    public static String loadFile(String filePath) throws IOException {
        InputStream is = null;
        try {
            is = new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
            is = ClassLoader.getSystemResourceAsStream(filePath);
        }
        if (is == null) {
            throw new FileNotFoundException("File[" + filePath + "] Not Exists!");
        }
        byte[] bytes = new byte[is.available()];
        is.read(bytes);
        return new String(bytes);
    }
}
