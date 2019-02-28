package com.github.m5.netutil.client;

import java.net.InetSocketAddress;
import java.util.Objects;

/**
 * @author xiaoyu
 */
public abstract class AbstractClient implements Client {
    private volatile boolean isClose;
    private InetSocketAddress remoteAddress;

    public AbstractClient(String remoteHost, int remotePort) {
        this(new InetSocketAddress(remoteHost, remotePort));
    }

    public AbstractClient(InetSocketAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
        doConnect();
    }

    @Override
    public void close() {
        if (isClose) {
            throw new IllegalStateException("The client has already been closed");
        }
        isClose = true;
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        Objects.requireNonNull(remoteAddress);
        return remoteAddress;
    }

    /**
     * doConnect
     */
    protected abstract void doConnect();
}
