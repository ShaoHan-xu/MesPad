package com.eeka.mespad.utils;

import java.lang.reflect.Field;
import java.util.HashMap;

public class ReflectUtil {

    /**
     * 获取对象的字段和对应的值
     */
    public static HashMap<String, Object> Reflect(Object obj) throws IllegalAccessException, IllegalArgumentException {
        HashMap<String, Object> map = new HashMap<>();
        if (obj == null)
            return null;
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Object _Object = field.get(obj);
            map.put(field.getName(), _Object);
        }
        return map;
    }
}
