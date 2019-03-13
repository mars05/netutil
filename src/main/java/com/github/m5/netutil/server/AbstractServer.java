package com.github.m5.netutil.server;

import javax.net.ssl.SSLContext;
import java.net.InetSocketAddress;
import java.util.Objects;

/**
 * @author xiaoyu
 */
abstract class AbstractServer implements Server {
    private volatile boolean isClose;
    private InetSocketAddress bindAddress;
    private SSLContext sslContext;

    public AbstractServer() {

    }

    public AbstractServer(int port) {
        this(new InetSocketAddress(port), null);
    }

    public AbstractServer(int port, SSLContext sslContext) {
        this(new InetSocketAddress(port), sslContext);
    }

    public AbstractServer(String host, int port) {
        this(new InetSocketAddress(host, port), null);
    }

    public AbstractServer(InetSocketAddress bindAddress, SSLContext sslContext) {
        this.bindAddress = bindAddress;
        this.sslContext = sslContext;
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

    protected SSLContext getSslContext() {
        return sslContext;
    }

    protected void setSslContext(SSLContext sslContext) {
        this.sslContext = sslContext;
    }

    @Override
    public boolean isSSL() {
        return sslContext != null;
    }
}
