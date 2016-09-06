/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author paulocanedo
 */
public final class ReflectionUtils {

    private ReflectionUtils() {
    }

    public static List<Field> readFields(Class c) {
        ArrayList<Field> list = new ArrayList<Field>();

        Field[] declaredFields = c.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            if (Collection.class.isAssignableFrom(declaredField.getType())) {
                continue;
            }

            list.add(declaredField);
        }
        return list;
    }

    public static Map<String, Object> readFieldsAndValues(Object o, Class c) {
        Map<String, Object> map = new HashMap<String, Object>();
        try {

            Field[] declaredFields = c.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                if (Collection.class.isAssignableFrom(declaredField.getType())) {
                    continue;
                }
                String fieldName = declaredField.getName();
                Method method = findMethod(c, "get", fieldName);
                map.put(declaredField.getName(), method.invoke(o));
            }

        } catch (Exception ex) {
            Logger.getLogger(ReflectionUtils.class.getName()).log(Level.SEVERE, null, ex);
        }

        return map;
    }

    public static Method findMethod(Class c, String prefixMethod, String attribute) {
        String methodName = prefixMethod + String.valueOf(attribute.charAt(0)).toUpperCase() + attribute.substring(1);

        Method method = findMethod(c, methodName);
        if (method != null) {
            return method;
        }

        Class superclass = c.getSuperclass();
        if (superclass != null && superclass != Object.class) {
            return findMethod(superclass, prefixMethod, attribute);
        }
        return null;
    }

    public static Method findMethod(Class c, String methodName) {
        Method[] methods = c.getMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        return null;
    }

    public static Object invokeMethod(Object object, Method m, Object... args) {
        try {
            return m.invoke(object, args);
        } catch (Exception ex) {
            Logger.getLogger(ReflectionUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        throw new RuntimeException("Fail to invoke method " + m.getName());
    }

    public static String splitCamelCase(String textCamelCase) {
        Stack<Integer> positionsCamelCase = new Stack<Integer>();

        boolean lastCharIsLowerCase = false;
        for (int i = 0; i < textCamelCase.length(); i++) {
            char charAt = textCamelCase.charAt(i);
            if (Character.isUpperCase(charAt)) {
                if (lastCharIsLowerCase) {
                    positionsCamelCase.push(i);
                }
            } else {
                lastCharIsLowerCase = true;
            }
        }

        StringBuilder splitText = new StringBuilder(textCamelCase);
        while (!positionsCamelCase.empty()) {
            int position = positionsCamelCase.pop();
            splitText.insert(position, "_");
        }
        return splitText.toString().toLowerCase();
    }

    public static void copyFromTo(Object source, Object destine) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Class srcClass = source.getClass();
        Class dstClass = destine.getClass();

        for (Field field : srcClass.getDeclaredFields()) {
            Method srcMethod = ReflectionUtils.findMethod(srcClass, "get", field.getName());
            Method dstMethod = ReflectionUtils.findMethod(dstClass, "set", field.getName());

            Object srcValue = srcMethod.invoke(source);
            dstMethod.invoke(destine, srcValue);
        }
    }
}
