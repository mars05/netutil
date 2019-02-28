package com.github.m5.netutil.proxy;

import java.lang.reflect.InvocationHandler;

/**
 * ProxyFactory
 *
 * @author xiaoyu
 * @see JdkProxyFactory
 * @see CglibProxyFactory
 * @see JavassistProxyFactory
 * @since 1.0.0
 */
public interface ProxyFactory {
    /**
     * getProxy
     *
     * @param clazz   要代理的类或接口
     * @param handler 处理器
     * @return 生成的代理对象
     */
    <T> T getProxy(Class<T> clazz, InvocationHandler handler);
}
