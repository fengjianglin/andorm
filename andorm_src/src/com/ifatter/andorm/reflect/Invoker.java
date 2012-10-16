
package com.ifatter.andorm.reflect;

import com.ifatter.andorm.orm.AndormException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class Invoker {

    private final Object receiver;

    private final Method method;

    public Invoker(Object receiver, Method method) {
        this.receiver = receiver;
        this.method = method;
    }

    public Object invoke(Object... params) {
        try {
            method.setAccessible(true);
            return method.invoke(receiver, params);
        } catch (InvocationTargetException e) {
            throw new AndormException(e.getMessage());
        } catch (IllegalAccessException ie) {
            throw new AndormException(ie.getMessage());
        }
    }

}
