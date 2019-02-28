package com.github.m5.netutil.handler;

import com.github.m5.netutil.channel.Channel;
import com.github.m5.netutil.channel.NettyChannel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.NetUtil;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author xiaoyu
 */
@ChannelHandler.Sharable
public abstract class AbstractNettyClientHandler extends ChannelDuplexHandler implements Handler {
    private ConcurrentMap<String, Channel> channelMap = new ConcurrentHashMap<>();
    private Handler handler = this;

    public AbstractNettyClientHandler() {
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = getOrAddChannelIfConnected(ctx.channel());
        try {
            handler.open(channel);
        } finally {
            removeChannelIfDisconnected(ctx.channel());
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = getOrAddChannelIfConnected(ctx.channel());
        try {
            handler.close(channel);
        } finally {
            removeChannelIfDisconnected(ctx.channel());
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel channel = getOrAddChannelIfConnected(ctx.channel());
        try {
            handler.receive(channel, msg);
        } finally {
            removeChannelIfDisconnected(ctx.channel());
        }
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        super.write(ctx, msg, promise);
        Channel channel = getOrAddChannelIfConnected(ctx.channel());
        try {
            handler.sent(channel, msg);
        } finally {
            removeChannelIfDisconnected(ctx.channel());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel channel = getOrAddChannelIfConnected(ctx.channel());
        try {
            handler.caught(channel, cause);
        } finally {
            removeChannelIfDisconnected(ctx.channel());
        }
    }

    private Channel getOrAddChannelIfConnected(io.netty.channel.Channel ch) {
        if (ch == null) {
            return null;
        }
        Channel nettyChannel = channelMap.get(NetUtil.toSocketAddressString((InetSocketAddress) ch.localAddress()));
        if (null == nettyChannel) {
            nettyChannel = new NettyChannel(ch);
            if (ch.isActive()) {
                Channel c = channelMap.putIfAbsent(NetUtil.toSocketAddressString((InetSocketAddress) ch.localAddress()), nettyChannel);
                if (c != null) {
                    nettyChannel = c;
                }
            }
        }
        return nettyChannel;
    }

    private void removeChannelIfDisconnected(io.netty.channel.Channel ch) {
        if (ch != null && !ch.isActive()) {
            channelMap.remove(NetUtil.toSocketAddressString((InetSocketAddress) ch.localAddress()));
        }
    }

    @Override
    public Map<String, Channel> getChannelMap() {
        return channelMap;
    }
}
