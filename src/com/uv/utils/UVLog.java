package com.uv.utils;

import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

/**
 * Created by uv2sun on 15/12/16.
 */
public class UVLog {
    private static Logger debugLogger = null;
    private static Logger errorLogger = null;
    private static Logger infoLogger = null;
    private static Logger warnLogger = null;

    static {
        loadLogger();
    }

    /**
     * 装载系统使用的 loger
     */
    static void loadLogger() {
        debugLogger = Logger.getLogger("debug");
        infoLogger = Logger.getLogger("info");
        errorLogger = Logger.getLogger("error");
        warnLogger = Logger.getLogger("warn");
    }

    /**
     * 记录操作过程中的错误信息记录，对应log4j中的error级别的log
     *
     * @param msg 信息
     */
    public static void error(Object msg) {
        errorLogger.error("[" + getParentClassname() + "]:" + msg);
    }

    /**
     * 记录操作过程中的错误的异常信息记录，对应log4j中的error级别的log
     *
     * @param e 要记录的异常信息
     */
    public static void error(Exception e) {
        errorLogger.error("[" + getParentClassname() + "]:" + getExceptionTrace(e));
    }

    /**
     * 记录操作过程中的错误的异常信息记录，对应log4j中的error级别的log
     *
     * @param e   要记录的异常信息
     * @param msg 要记录的信息
     */
    public static void error(Exception e, Object msg) {
        errorLogger.error("[" + getParentClassname() + "]:" + msg + "\n" + getExceptionTrace(e));
    }

    /**
     * 记录操作过程中的警告信息记录，对应log4j中的error级别的log
     *
     * @param msg 信息
     */
    public static void warn(Object msg) {
        errorLogger.error("[" + getParentClassname() + "]:" + msg);
    }

    /**
     * 记录操作过程中的警告的信息记录，对应log4j中的error级别的log
     *
     * @param e 要记录的异常信息
     */
    public static void warn(Exception e) {
        errorLogger.error("[" + getParentClassname() + "]:" + getExceptionTrace(e));
    }

    /**
     * 记录操作过程中的警告的信息记录，对应log4j中的error级别的log
     *
     * @param e   要记录的异常信息
     * @param msg 要记录的信息
     */
    public static void warn(Exception e, Object msg) {
        errorLogger.error("[" + getParentClassname() + "]:" + msg + "\n" + getExceptionTrace(e));
    }

    /**
     * 记录操作过程中的调试信息，对应log4j中的debug级别的log
     *
     * @param msg 要记录信息
     */
    public static void debug(Object msg) {
        debugLogger.debug("[" + getParentClassname() + "]:" + msg);
    }

    public static void debug(Object obj, String msg) {
        infoLogger.info("[" + getParentClassname() + "](" + obj.toString() + ")" + msg);
    }

    /**
     * 记录调试中的异常信息，对应log4j中的debug级别的log
     *
     * @param e 要记录的异常信息
     */
    public static void debug(Exception e) {
        debugLogger.debug(getExceptionTrace(e));
    }

    /**
     * 记录调试中的异常信息，对应log4j中的debug级别的log
     *
     * @param e   要记录的异常信息
     * @param msg 要记录的信息
     */
    public static void debug(Exception e, Object msg) {
        debugLogger.debug("[" + getParentClassname() + "]:" + msg + "\n" + getExceptionTrace(e));
    }

    /**
     * 系统信息日志纪录，对应log4j中是info级别的log
     *
     * @param msg 信息
     */
    public static void info(Object msg) {
        infoLogger.info("[" + getParentClassname() + "]:" + msg);
    }

    /**
     * 系统信息异常日志纪录，对应log4j中是info级别的log
     *
     * @param e 要记录的异常信息
     */
    public static void info(Exception e) {
        infoLogger.info("[" + getParentClassname() + "]:" + getExceptionTrace(e));
    }

    /**
     * 系统信息异常日志纪录，对应log4j中是info级别的log
     *
     * @param e   要记录的异常信息
     * @param msg 要记录的信息
     */
    public static void info(Exception e, Object msg) {
        infoLogger.info("[" + getParentClassname() + "]:" + msg + "\n" + getExceptionTrace(e));
    }

    public static void info(Object obj, String msg) {
        infoLogger.info("[" + getParentClassname() + "](" + obj.toString() + ")" + msg);
    }

    /**
     * 输出异常信息 替代 e.printStackTrace();
     *
     * @param e 异常
     */
    public static void exOut(Exception e) {
        String s = getExceptionTrace(e);
        errorLogger.error("[" + getParentClassname() + "]:" + s);

    }

    private static String getExceptionTrace(Exception e) {
        String s = null;
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        PrintWriter wrt = new PrintWriter(bout);
        e.printStackTrace(wrt);
        wrt.close();
        s = bout.toString();
        return s;
    }

    private static String getParentClassname() {
        StackTraceElement stack[] = (new Throwable()).getStackTrace();
        return stack[2].getClassName();

    }


    public static void main(String[] args) {
        UVLog.debug("123");
    }
}
