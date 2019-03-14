package com.github.m5.netutil.proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

/**
 * @author xiaoyu
 */
public class CglibProxyFactory implements ProxyFactory {

    @Override
    public <T> T getProxy(Class<T> clazz, java.lang.reflect.InvocationHandler handler) {
        Enhancer enhancer = new Enhancer();
        if (clazz.isInterface()) {
            enhancer.setInterfaces(new Class[]{clazz});
        } else {
            enhancer.setSuperclass(clazz);
        }
        enhancer.setCallback((MethodInterceptor) (obj, method, args, proxy) -> handler.invoke(obj, method, args));
        return (T) enhancer.create();
    }
}
