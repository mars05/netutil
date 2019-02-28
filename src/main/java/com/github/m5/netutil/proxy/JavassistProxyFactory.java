//package com.github.m5.netutil.proxy;
//
//import javassist.util.proxy.MethodHandler;
//import javassist.util.proxy.Proxy;
//
//import java.lang.reflect.InvocationHandler;
//import java.lang.reflect.Method;
//import java.lang.reflect.Modifier;
//
///**
// * @author xiaoyu
// */
//public class JavassistProxyFactory implements ProxyFactory {
//    @Override
//    public <T> T getProxy(Class<T> clazz, InvocationHandler handler) {
//        javassist.util.proxy.ProxyFactory proxyFactory = new javassist.util.proxy.ProxyFactory();
//        if (Modifier.isInterface(clazz.getModifiers())) {
//            proxyFactory.setInterfaces(new Class[]{clazz});
//        } else {
//            proxyFactory.setSuperclass(clazz);
//        }
//
//        try {
//            Proxy proxy = (Proxy) proxyFactory.createClass().newInstance();
//            proxy.setHandler(new MethodHandler() {
//                @Override
//                public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable {
//                    return handler.invoke(self, thisMethod, args);
//                }
//            });
//            return (T) proxy;
//        } catch (Exception e) {
//            throw new RuntimeException(e.getMessage(), e);
//        }
//    }
//}
