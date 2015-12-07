
package com.ifatter.andorm.reflect;

import com.ifatter.andorm.AndormException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class Reflactor {

    private final Class<?> clazz;

    private Reflactor(Class<?> clazz) {
        this.clazz = clazz;
    }

    public final Field returnField(String fieldName) {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            throw new AndormException(clazz.getName());
        }
    }

    public final Method returnSetMethodOf(String field) {
        Field f = returnField(field);
        return returnSetMethodOf(f);
    }

    public final Method returnSetMethodOf(Field field) {
        StringBuilder methodName = new StringBuilder("set");
        methodName.append(String.valueOf(field.getName().charAt(0)).toUpperCase());
        methodName.append(field.getName().substring(1));
        try {
            return clazz.getDeclaredMethod(methodName.toString(), field.getType());
        } catch (NoSuchMethodException e) {
            throw new AndormException(clazz.getName());
        }
    }

    public final Method returnGetMethodOf(String field) {
        Field f = returnField(field);
        return returnGetMethodOf(f);
    }

    public final Method returnGetMethodOf(Field field) {
        StringBuilder methodName = new StringBuilder("get");
        methodName.append(String.valueOf(field.getName().charAt(0)).toUpperCase());
        methodName.append(field.getName().substring(1));
        try {
            return clazz.getDeclaredMethod(methodName.toString(), new Class<?>[] {});
        } catch (NoSuchMethodException e) {
            throw new AndormException(clazz.getName());
        }
    }

    public final Method returnMethod(String methodName, Class<?>... paramTypes) {
        try {
            return clazz.getDeclaredMethod(methodName, paramTypes);
        } catch (NoSuchMethodException e) {
            throw new AndormException(clazz.getName());
        }
    }

    public static final Invoker newInvoker(Object receiver, Method method) {
        return new Invoker(receiver, method);
    }

    public final static Reflactor in(Class<?> clazz) {
        return new Reflactor(clazz);
    }

    public final static Object getFieldValue(Object of, Field field) {
        try {
            field.setAccessible(true);
            return field.get(of);
        } catch (IllegalAccessException e) {
            throw new AndormException(field.getName() + e.getMessage());
        }
    }

    public final static <T> T newInstance(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException ie) {
            ie.printStackTrace();
            throw new AndormException(clazz.getCanonicalName());
        } catch (IllegalAccessException iae) {
            throw new AndormException(clazz.getCanonicalName());
        }
    }

}
