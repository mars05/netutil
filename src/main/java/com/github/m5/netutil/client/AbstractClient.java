package com.github.m5.netutil.client;

import javax.net.ssl.SSLContext;
import java.net.InetSocketAddress;
import java.util.Objects;

/**
 * @author xiaoyu
 */
public abstract class AbstractClient implements Client {
    private volatile boolean isClose;
    private InetSocketAddress remoteAddress;
    private SSLContext sslContext;

    public AbstractClient(String remoteHost, int remotePort) {
        this(new InetSocketAddress(remoteHost, remotePort));
    }

    public AbstractClient(InetSocketAddress remoteAddress) {
        this(remoteAddress, null);
    }

    public AbstractClient(String remoteHost, int remotePort, SSLContext sslContext) {
        this(new InetSocketAddress(remoteHost, remotePort), sslContext);
    }

    public AbstractClient(InetSocketAddress remoteAddress, SSLContext sslContext) {
        this.remoteAddress = remoteAddress;
        this.sslContext = sslContext;
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
