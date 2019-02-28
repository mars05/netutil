package com.github.m5.netutil.proxy;

import java.lang.reflect.Method;

/**
 * @author xiaoyu
 */
public abstract class InvocationHandler implements java.lang.reflect.InvocationHandler {
    protected abstract Object invoke0(Object proxy, Method method, Object[] args) throws Throwable;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (isEqualsMethod(method)) {
            return equals(proxy);
        } else if (isHashCodeMethod(method)) {
            return hashCode();
        } else if (isToStringMethod(method)) {
            return toString();
        }
        try {
            return invoke0(proxy, method, args);
        } catch (Throwable throwable) {
            throw throwable;
        }
    }

    private boolean isEqualsMethod(Method method) {
        if (method == null || !method.getName().equals("equals")) {
            return false;
        }
        Class<?>[] paramTypes = method.getParameterTypes();
        return (paramTypes.length == 1 && paramTypes[0] == Object.class);
    }

    private boolean isHashCodeMethod(Method method) {
        return (method != null && method.getName().equals("hashCode") && method.getParameterCount() == 0);
    }

    private boolean isToStringMethod(Method method) {
        return (method != null && method.getName().equals("toString") && method.getParameterCount() == 0);
    }

}
