package com.github.m5.netutil.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;

/**
 * @author xiaoyu
 */
public class JdkProxyFactory implements ProxyFactory {

    @Override
    public <T> T getProxy(Class<T> clazz, InvocationHandler handler) {
        Class<?>[] classes;
        if (Modifier.isInterface(clazz.getModifiers())) {
            classes = new Class[]{clazz};
        } else {
            classes = clazz.getInterfaces();
        }
        return (T) Proxy.newProxyInstance(this.getClass().getClassLoader(), classes, handler);
    }
}
