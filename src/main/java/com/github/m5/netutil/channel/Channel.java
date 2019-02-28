package com.github.m5.netutil.channel;

import java.net.InetSocketAddress;

/**
 * @author xiaoyu
 */
public interface Channel {
    /**
     * isOpen
     *
     * @return
     */
    boolean isOpen();

    void send(Object message);

    void sendAndClose(Object message);

    /**
     * getLocalAddress
     *
     * @return
     */
    InetSocketAddress getLocalAddress();

    /**
     * getRemoteAddress
     *
     * @return
     */
    InetSocketAddress getRemoteAddress();

    void close();
}
