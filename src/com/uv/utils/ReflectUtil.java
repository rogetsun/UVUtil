package com.uv.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by uv2sun on 2016/11/30.
 */
public class ReflectUtil {
    /**
     * 执行对象o的propertyName的set方法，赋值value
     * o.setPropertyName(value)
     *
     * @param o
     * @param propertyName
     * @param value
     * @return true:设置成功，false:设置失败
     */
    public static boolean set(Object o, String propertyName, Object value) {
        String methodName = "set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
        Method[] ms = o.getClass().getMethods();
        for (int idx = 0; idx < ms.length; idx++) {
            if (ms[idx].getName().equals(methodName)) {
                Method m = ms[idx];
                if (m.getParameterCount() != 1) continue;
                Class paramClass = m.getParameterTypes()[0];
                try {
                    switch (paramClass.getName()) {
                        case "int":
                        case "java.lang.Integer":
                            m.invoke(o, Integer.valueOf(value.toString()));
                            return true;
                        case "long":
                        case "java.lang.Long":
                            m.invoke(o, Long.valueOf(value.toString()));
                            return true;
                        case "double":
                        case "java.lang.Double":
                            m.invoke(o, Double.valueOf(value.toString()));
                            return true;
                        case "float":
                        case "java.lang.Float":
                            m.invoke(o, Float.valueOf(value.toString()));
                            return true;
                        case "byte":
                        case "java.lang.Byte":
                            m.invoke(o, Byte.valueOf(value.toString()));
                            return true;
                        case "java.lang.String":
                            m.invoke(o, value.toString());
                            return true;
                    }
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
}
