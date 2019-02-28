package com.github.m5.netutil.channel;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import java.net.InetSocketAddress;

/**
 * @author xiaoyu
 */
public final class NettyChannel implements Channel {
    private static final int DEFAULT_TIMEOUT = 1000;
    private io.netty.channel.Channel channel;

    public NettyChannel(io.netty.channel.Channel channel) {
        this.channel = channel;
    }

    @Override
    public void close() {
        channel.close();
    }

    @Override
    public boolean isOpen() {
        return channel.isActive();
    }

    @Override
    public void send(Object message) {
        if (!channel.isActive()) {
            throw new IllegalStateException("Channel closed");
        }
        ChannelFuture future = channel.writeAndFlush(message);
        future.awaitUninterruptibly(DEFAULT_TIMEOUT);
        if (!future.isSuccess()) {
            Throwable cause = future.cause();
            if (cause == null) {
                throw new IllegalStateException("Fail to send");
            }
            throw new IllegalStateException(future.cause().getMessage(), future.cause());
        }
    }

    @Override
    public void sendAndClose(Object message) {
        if (!channel.isActive()) {
            throw new IllegalStateException("Channel closed");
        }
        ChannelFuture future = channel.writeAndFlush(message).addListener(ChannelFutureListener.CLOSE);
        future.awaitUninterruptibly(DEFAULT_TIMEOUT);
        if (!future.isSuccess()) {
            Throwable cause = future.cause();
            if (cause == null) {
                throw new IllegalStateException("Fail to send");
            }
            throw new IllegalStateException(future.cause().getMessage(), future.cause());
        }
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return (InetSocketAddress) channel.localAddress();
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return (InetSocketAddress) channel.remoteAddress();
    }

    @Override
    public int hashCode() {
        return channel.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        return channel.hashCode() == obj.hashCode();
    }

    @Override
    public String toString() {
        return "NettyChannel [channel=" + channel + "]";
    }

}
