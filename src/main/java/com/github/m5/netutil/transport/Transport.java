package com.github.m5.netutil.transport;

import com.github.m5.netutil.channel.Channel;
import com.github.m5.netutil.codec.Codec;
import com.github.m5.netutil.handler.Handler;

import java.net.InetSocketAddress;

/**
 * @author xiaoyu
 */
public interface Transport {
    boolean isOpen();

    boolean isSSL();

    boolean isTransportable();

    InetSocketAddress getLocalAddress();

    Channel getChannel();

    Codec getCodec();

    Handler getHandler();

    void close();
}
