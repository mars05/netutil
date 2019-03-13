package com.github.m5.netutil.ws;

import com.github.m5.netutil.channel.Channel;
import com.github.m5.netutil.exception.RemoteException;
import com.github.m5.netutil.handler.AbstractNettyServerHandler;
import com.github.m5.netutil.util.NamedThreadFactory;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author xiaoyu
 */
class WebSocketServerHandler extends AbstractNettyServerHandler {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketServerHandler.class);
    private WebSocketRequestHandler handler;
    private ExecutorService executorService = new ThreadPoolExecutor(200, 200,
            0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), new NamedThreadFactory("ws"));

    {
        executorService.execute(() -> {
        });
    }

    public WebSocketServerHandler() {
    }

    private static final ByteBuf HEARTBEAT_SEQUENCE = Unpooled.unreleasableBuffer(
            Unpooled.copiedBuffer("HEARTBEAT", CharsetUtil.ISO_8859_1));

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            ctx.channel().close();
        } else if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            ctx.pipeline().remove("fullHttpRequestHandler");
            WebSocketChannel info = ctx.channel().attr(Constants.WEB_SOCKET_CHANNEL_ATTRIBUTE_KEY).get();
            handler.putWebSocketChannel(info);
            handler.onOpen(info);
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        WebSocketChannel info = ctx.channel().attr(Constants.WEB_SOCKET_CHANNEL_ATTRIBUTE_KEY).getAndSet(null);
        if(info==null){
            return;
        }
        if (info.getCloseStatus() == null) {
            info.setCloseStatus(CloseStatus.NO_CLOSE_FRAME);
        }
        try {
            handler.onClose(info, info.getCloseStatus().getCode(), info.getCloseStatus().getReason(), info.getCloseStatus().isRemote());
        } finally {
            handler.removeWebSocketChannel(info);
        }
    }

    @Override
    public void open(Channel channel) throws RemoteException {
        logger.info("{} is opened", channel);
    }


    @Override
    public void close(Channel channel) throws RemoteException {
        logger.warn("{} is closed", channel);
    }

    @Override
    public void sent(Channel channel, Object message) throws RemoteException {
        if (logger.isDebugEnabled()) {
            logger.debug("{} sent message: {}", channel, message);
        }
    }

    @Override
    public void receive(Channel channel, Object message) throws RemoteException {
        if (logger.isDebugEnabled()) {
            logger.debug("{} receive message: {}", channel, message);
        }
        if (message instanceof WebSocketRequest) {
            WebSocketRequest request = (WebSocketRequest) message;
            handler.onMessage(request.getWebSocketChannel(), request.getMessage());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        handler.onError(ctx.channel().attr(Constants.WEB_SOCKET_CHANNEL_ATTRIBUTE_KEY).get(), cause);
    }

    @Override
    public void caught(Channel channel, Throwable cause) throws RemoteException {

    }

    public void setHandler(WebSocketRequestHandler handler) {
        this.handler = handler;
    }

    @Override
    public ExecutorService getExecutorService() {
        return executorService;
    }
}
