package com.github.m5.netutil.rpc;

import com.github.m5.netutil.codec.Codec;
import com.github.m5.netutil.handler.Handler;
import com.github.m5.netutil.rpc.config.ServerConfig;
import com.github.m5.netutil.server.AbstractNettyServer;
import com.github.m5.netutil.util.SerializationType;

import javax.net.ssl.SSLContext;
import java.net.InetSocketAddress;

/**
 * @author xiaoyu
 */
public class YrpcServer extends AbstractNettyServer {

    private static final YrpcServerHandler handler = new YrpcServerHandler();

    public YrpcServer(int port, ServerConfig config) {
        handler.setServerConfig(config);
        bind(new InetSocketAddress(port));
    }

    public YrpcServer(int port, SSLContext sslContext, ServerConfig config) {
        handler.setServerConfig(config);
        setSslContext(sslContext);
        bind(new InetSocketAddress(port));
    }

    @Override
    public Codec getCodec() {
        return new YrpcServerCodec(SerializationType.JSON);
    }

    @Override
    public Handler getHandler() {
        return handler;
    }


}
