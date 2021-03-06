package com.github.m5.netutil.ws;

import com.github.m5.netutil.codec.Codec;
import com.github.m5.netutil.handler.Handler;
import com.github.m5.netutil.server.AbstractNettyServer;

import javax.net.ssl.SSLContext;
import java.net.InetSocketAddress;

/**
 * @author xiaoyu
 */
public class WebSocketServer extends AbstractNettyServer {
    private static final WebSocketServerCodec WEB_SOCKET_SERVER_CODEC = new WebSocketServerCodec();
    private static final WebSocketServerHandler WEB_SOCKET_SERVER_HANDLER = new WebSocketServerHandler();

    public WebSocketServer(int port, WebSocketRequestHandler handler) {
        this(port, null, handler);
    }

    public WebSocketServer(int port, SSLContext sslContext, WebSocketRequestHandler handler) {
        WEB_SOCKET_SERVER_HANDLER.setHandler(handler);
        setSslContext(sslContext);
        bind(new InetSocketAddress(port));
    }

    @Override
    public Codec getCodec() {
        return WEB_SOCKET_SERVER_CODEC;
    }

    @Override
    public Handler getHandler() {
        return WEB_SOCKET_SERVER_HANDLER;
    }

}
