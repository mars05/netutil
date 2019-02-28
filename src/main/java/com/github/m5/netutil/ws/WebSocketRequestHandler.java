package com.github.m5.netutil.ws;

import com.github.m5.netutil.channel.Channel;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author xiaoyu
 */
public abstract class WebSocketRequestHandler {
    private ConcurrentMap<Channel, WebSocketChannel> webSocketChannels = new ConcurrentHashMap<>();

    public abstract void onOpen(WebSocketChannel webSocketChannel);

    public abstract void onMessage(WebSocketChannel webSocketChannel, String message);

    public abstract void onClose(WebSocketChannel webSocketChannel, int code, String reason, boolean remote);

    public abstract void onError(WebSocketChannel webSocketChannel, Throwable throwable);

    void putWebSocketChannel(WebSocketChannel webSocketChannel) {
        webSocketChannels.putIfAbsent(webSocketChannel.getChannel(), webSocketChannel);
    }

    void removeWebSocketChannel(WebSocketChannel webSocketChannel) {
        webSocketChannels.remove(webSocketChannel.getChannel());
    }

    public Collection<WebSocketChannel> getWebSocketChannels() {
        return webSocketChannels.values();
    }
}
