package com.github.m5.netutil.server;

import java.net.InetSocketAddress;
import java.util.Objects;

/**
 * @author xiaoyu
 */
abstract class AbstractServer implements Server {
    private volatile boolean isClose;
    private InetSocketAddress bindAddress;

    protected AbstractServer() {
    }

    public AbstractServer(int port) {
        this(new InetSocketAddress(port));
    }

    public AbstractServer(String host, int port) {
        this(new InetSocketAddress(host, port));
    }

    public AbstractServer(InetSocketAddress bindAddress) {
        this.bindAddress = bindAddress;
        doBind();
    }

    @Override
    public void close() {
        if (isClose) {
            throw new IllegalStateException("The server has already been closed");
        }
        isClose = true;
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        Objects.requireNonNull(bindAddress);
        return bindAddress;
    }

    /**
     * bind
     */
    protected void bind(InetSocketAddress bindAddress) {
        this.bindAddress = bindAddress;
        doBind();
    }

    /**
     * doBind
     */
    protected abstract void doBind();

}
