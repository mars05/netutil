package com.github.m5.netutil.rpc;

import com.github.m5.netutil.client.Client;
import com.github.m5.netutil.exception.RemoteException;
import com.github.m5.netutil.proxy.InvocationHandler;
import com.github.m5.netutil.proxy.JdkProxyFactory;
import com.github.m5.netutil.proxy.ProxyFactory;
import io.netty.util.internal.StringUtil;

import java.io.BufferedReader;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author xiaoyu
 */
public class ServiceProxyFactory {
    private static final ProxyFactory proxyFactory = new JdkProxyFactory();
    private Client client;

    public ServiceProxyFactory(Client client) {
        this.client = client;
    }

    public <T> T newServiceProxy(Class<T> interfaceClass) {
        T service = proxyFactory.getProxy(interfaceClass, new InvocationHandler() {
            @Override
            protected Object invoke0(Object proxy, Method method, Object[] args) throws Throwable {
                YrpcRequest request = new YrpcRequest();
                request.setInterfaceName(interfaceClass.getName());
                request.setMethodName(method.getName());
                request.setParams(args);
                YrpcResponse response = client.send(request);
                if (!StringUtil.isNullOrEmpty(response.getErrMsg())) {
                    RpcException e = new RpcException(response.getRequestId(), resolverRpcExceptionMessageByStackTrace(response.getErrMsg()));
                    e.initCause(new RemoteException(response.getErrMsg()));
                    throw e;
                }
                return response.getResult();
            }
        });
        return service;
    }

    private String resolverRpcExceptionMessageByStackTrace(String msg) throws Exception {
        if (StringUtil.isNullOrEmpty(msg)) {
            return null;
        }
        String s = new BufferedReader(new StringReader(msg)).readLine();
        s = s.replaceAll("^.*RpcException", "");
        if (StringUtil.length(s) == 0) {
            s = null;
        }
        if (s != null) {
            s = s.replaceAll("^: ", "");
        }
        return s;
    }

    public static void main(String[] args) {
        RuntimeException exception = new RuntimeException();
        List<StackTraceElement> ls = new ArrayList();
        ls.add(new StackTraceElement("aaaa.aaaa", "bbb", "ccc.ccc.ccc.class", 332));
        ls.add(new StackTraceElement("aaaa.aaaa", "bbb", "ccc.ccc.ccc.class", 332));
        exception.setStackTrace(ls.toArray(new StackTraceElement[ls.size()]));
        exception.printStackTrace();
    }
}
