package com.github.m5.netutil.rpc;

import com.github.m5.netutil.channel.Channel;
import com.github.m5.netutil.exception.CodecException;
import com.github.m5.netutil.exception.RemoteException;
import com.github.m5.netutil.handler.AbstractNettyServerHandler;
import com.github.m5.netutil.rpc.config.ServerConfig;
import com.github.m5.netutil.util.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author xiaoyu
 */
class YrpcServerHandler extends AbstractNettyServerHandler {
    private static final Logger logger = LoggerFactory.getLogger(YrpcServerHandler.class);
    private ServerConfig serverConfig;
    private ExecutorService executorService = new ThreadPoolExecutor(100, 100,
            0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), new NamedThreadFactory("yrpc"));

    {
        executorService.execute(() -> {
        });
    }

    public YrpcServerHandler() {
    }

    @Override
    public void open(Channel channel) throws RemoteException {
        logger.info("{} is opened", channel);
    }

    @Override
    public void close(Channel channel) throws RemoteException {
        logger.warn("{} is closed", channel);
    }

    @Override
    public void sent(Channel channel, Object message) throws RemoteException {
        if (logger.isDebugEnabled()) {
            logger.debug("{} sent message: {}", channel, message);
        }
    }

    private void checkRequestMethod(YrpcRequest request) throws Exception {
        Class<?> aClass = Class.forName(request.getInterfaceName());
        Method[] methods = aClass.getMethods();
        if (methods.length == 0) {
            throw new com.github.m5.netutil.exception.CodecException(request.getRequestId(), "The " + request.getInterfaceName() + " interface does not have any methods");
        }
    }

    @Override
    public void receive(Channel channel, Object message) throws RemoteException {
        if (logger.isDebugEnabled()) {
            logger.debug("{} receive message: {}", channel, message);
        }
        YrpcRequest request = (YrpcRequest) message;
        Object serviceImpl = serverConfig.getService(request.getInterfaceName(), request.getGroup(), request.getVersion());
        if (logger.isDebugEnabled()) {
            logger.debug("interface: {}, interfaceImpl: {}", request.getInterfaceName(), serviceImpl);
        }
        YrpcResponse response = null;

        try {
            Objects.requireNonNull(serviceImpl, "No service running");
            checkRequestMethod(request);
            Class<?>[] paramsClass = null;
            Object[] args = request.getParams();
            if (args != null) {
                paramsClass = new Class[args.length];
                for (int i = 0; i < args.length; i++) {
                    paramsClass[i] = args[i].getClass();
                }
            }
            Method method = serviceImpl.getClass().getMethod(request.getMethodName(), paramsClass);
            Object result = null;
            try {
                result = method.invoke(serviceImpl, args);
            } catch (InvocationTargetException e) {
                throw e.getCause();
            }
            if (logger.isDebugEnabled()) {
                logger.debug("interface: {}, interfaceImpl: {}, method: {}, params: {}, execute result: {}"
                        , request.getInterfaceName(), serviceImpl, request.getMethodName(), Arrays.asList(paramsClass), result);
            }
            response = new YrpcResponse();
            response.setRequestId(request.getRequestId());
            response.setResult(result);
        } catch (Throwable e) {
            throw new RpcException(request.getRequestId(), e.getMessage(), e);
        }
        if (response != null) {
            channel.send(response);
        }
    }

    @Override
    public void caught(Channel channel, Throwable cause) throws RemoteException {
        logger.error("" + channel, cause);
        if (cause instanceof io.netty.handler.codec.CodecException) {
            cause = cause.getCause();
        }
        YrpcResponse response = null;
        if (cause instanceof CodecException) {
            CodecException exception = (CodecException) cause;
            response = new YrpcResponse();
            response.setRequestId(exception.getRequestId());
            StringWriter stringWriter = new StringWriter();
            cause.printStackTrace(new PrintWriter(stringWriter));
            response.setRequestId(exception.getRequestId());
            response.setErrMsg(stringWriter.toString());
        } else if (cause instanceof RpcException) {
            RpcException exception = (RpcException) cause;
            response = new YrpcResponse();
            response.setRequestId(exception.getRequestId());
            StringWriter stringWriter = new StringWriter();
            cause.printStackTrace(new PrintWriter(stringWriter));
            response.setRequestId(exception.getRequestId());
            response.setErrMsg(stringWriter.toString());
        } else {
            try {
                channel.close();
                return;
            } catch (Throwable e) {
            }
        }
        if (response != null) {
            channel.send(response);
        }
    }

    public void setServerConfig(ServerConfig config) {
        this.serverConfig = config;
    }

    @Override
    public ExecutorService getExecutorService() {
        return executorService;
    }
}
