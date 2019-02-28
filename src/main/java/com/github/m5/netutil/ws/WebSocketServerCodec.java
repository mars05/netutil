package com.github.m5.netutil.ws;

import com.github.m5.netutil.channel.NettyChannel;
import com.github.m5.netutil.codec.Codec;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.ReferenceCounted;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author xiaoyu
 */
public final class WebSocketServerCodec extends ChannelInitializer<SocketChannel> implements Codec {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketServerCodec.class);

    public WebSocketServerCodec() {
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(new IdleStateHandler(0, 0, 300));
        ch.pipeline().addLast("httpCodec", new HttpServerCodec())
                .addLast("aggregator", new HttpObjectAggregator(65536))
                // ChunkedWriteHandler：向客户端发送HTML5文件，ChunkedWriteHandler分块写处理，文件过大会将内存撑爆
                .addLast("chunkedWriteHandler", new ChunkedWriteHandler())
                .addLast("fullHttpRequestHandler", new ChannelDuplexHandler() {
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        if (msg instanceof FullHttpRequest) {
                            FullHttpRequest request = (FullHttpRequest) msg;
                            WebSocketChannel info = new WebSocketChannel();
                            info.setChannel(new NettyChannel(ctx.channel()));
                            info.setUri(request.uri());
                            ctx.channel().attr(Constants.WEB_SOCKET_CHANNEL_ATTRIBUTE_KEY).set(info);
                        }
                        ctx.fireChannelRead(msg);
                    }
                })
                .addLast("webSocketServerProtocolHandler", new WebSocketServerProtocolHandler("/", true) {
                    @Override
                    protected void decode(ChannelHandlerContext ctx, WebSocketFrame frame, List<Object> out) throws Exception {
                        if (frame instanceof CloseWebSocketFrame) {
                            WebSocketChannel info = ctx.channel().attr(Constants.WEB_SOCKET_CHANNEL_ATTRIBUTE_KEY).get();
                            CloseWebSocketFrame cf = (CloseWebSocketFrame) frame;
                            if (((CloseWebSocketFrame) frame).statusCode() == -1) {
                                frame = new CloseWebSocketFrame(1000, cf.reasonText());
                            }
                            info.setCloseStatus(new CloseStatus(((CloseWebSocketFrame) frame).statusCode(), ((CloseWebSocketFrame) frame).reasonText()));
                            info.getCloseStatus().setRemote(true);
                        }
                        super.decode(ctx, frame, out);
                    }
                })
                .addLast("messageToMessageCodec", new MessageToMessageCodec<TextWebSocketFrame, Object>() {
                    @Override
                    protected void decode(ChannelHandlerContext ctx, TextWebSocketFrame msg, List<Object> out) throws Exception {
                        WebSocketRequest request = new WebSocketRequest();
                        request.setWebSocketChannel(ctx.channel().attr(Constants.WEB_SOCKET_CHANNEL_ATTRIBUTE_KEY).get());
                        request.setMessage(msg.text());
                        out.add(request);
                    }

                    @Override
                    protected void encode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {
                        if (msg instanceof ReferenceCounted) {
                            ReferenceCountUtil.retain(msg);
                            out.add(msg);
                        } else if (msg instanceof String) {
                            out.add(new TextWebSocketFrame((String) msg));
                        } else {
                            throw new UnsupportedOperationException("仅支持String类型数据");
                        }

                    }
                });
    }

    @Override
    public byte[] encode(Object message) {
        return null;
    }

    @Override
    public Object decode(byte[] bytes) {
        return null;
    }

}

