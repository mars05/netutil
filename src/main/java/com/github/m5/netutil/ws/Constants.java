package com.github.m5.netutil.ws;

import io.netty.util.AttributeKey;

/**
 * @author xiaoyu
 */
public interface Constants {
    AttributeKey<WebSocketChannel> WEB_SOCKET_CHANNEL_ATTRIBUTE_KEY = AttributeKey.newInstance("WSI");
}
