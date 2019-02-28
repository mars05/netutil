package com.github.m5.netutil.rpc;

import com.github.m5.netutil.client.AbstractNettyClient;
import com.github.m5.netutil.codec.Codec;
import com.github.m5.netutil.handler.Handler;
import com.github.m5.netutil.util.SerializationType;

import java.net.InetSocketAddress;

/**
 * @author xiaoyu
 */
public class YrpcClient extends AbstractNettyClient {

    private static final Handler handler = new YrpcClientHandler();

    public YrpcClient(String remoteHost, int remotePort) {
        super(remoteHost, remotePort);
    }

    public YrpcClient(InetSocketAddress remoteAddress) {
        super(remoteAddress);
    }

    @Override
    public Codec getCodec() {
        return new YrpcClientCodec(SerializationType.JSON);
    }

    @Override
    public Handler getHandler() {
        return handler;
    }


}
