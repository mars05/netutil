package com.github.m5.netutil.client;

import com.github.m5.netutil.channel.Channel;
import com.github.m5.netutil.channel.NettyChannel;
import com.github.m5.netutil.exception.NetException;
import com.github.m5.netutil.request.Request;
import com.github.m5.netutil.response.Response;
import com.github.m5.netutil.util.Constants;
import com.github.m5.netutil.util.NamedThreadFactory;
import com.github.m5.netutil.util.ResultSynchronizer;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.NetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.Future;

/**
 * @author xiaoyu
 */
public abstract class AbstractNettyClient extends AbstractClient {
    private static final Logger logger = LoggerFactory.getLogger(AbstractNettyClient.class);

    private static volatile Bootstrap bootstrap;

    private Channel channel;
    private volatile boolean isTransportable;

    public AbstractNettyClient(String remoteHost, int remotePort) {
        super(remoteHost, remotePort);
    }

    public AbstractNettyClient(InetSocketAddress remoteAddress) {
        super(remoteAddress);
    }

    private void initBootstrap() {
        if (null == bootstrap) {
            synchronized (getClass()) {
                if (null == bootstrap) {
                    bootstrap = new Bootstrap();
                    NioEventLoopGroup workerGroup = new NioEventLoopGroup(Constants.DEFAULT_EVENT_LOOP_THREADS, new NamedThreadFactory("NettyClientWorker", true));
                    bootstrap.group(workerGroup)
                            .channel(NioSocketChannel.class)
                            .option(ChannelOption.SO_KEEPALIVE, true)
//                            .option(ChannelOption.TCP_NODELAY, true)
                            .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                            .handler(new ChannelInitializer<SocketChannel>() {
                                @Override
                                protected void initChannel(SocketChannel ch) throws Exception {
                                    ChannelPipeline pipeline = ch.pipeline();
                                    pipeline.addLast("codec", (ChannelHandler) getCodec())
                                            .addLast("handler", (ChannelHandler) getHandler());
                                }
                            });
                }
            }
        }
    }

    @Override
    protected void doConnect() {
        initBootstrap();
        ChannelFuture channelFuture = bootstrap.connect(getRemoteAddress()).awaitUninterruptibly();
        if (channelFuture.isSuccess()) {
            isTransportable = true;
        } else {
            logger.error(channelFuture.cause().getMessage(), channelFuture.cause());
            throw new RuntimeException(channelFuture.cause().getMessage(), channelFuture.cause());
        }
        io.netty.channel.Channel ch = channelFuture.channel();
        Channel nettyChannel = getHandler().getChannelMap().get(NetUtil.toSocketAddressString((InetSocketAddress) ch.localAddress()));
        if (null == nettyChannel) {
            nettyChannel = new NettyChannel(ch);
            if (ch.isActive()) {
                Channel c = getHandler().getChannelMap().putIfAbsent(NetUtil.toSocketAddressString((InetSocketAddress) ch.localAddress()), nettyChannel);
                if (c != null) {
                    nettyChannel = c;
                }
            }
        }
        channel = nettyChannel;
    }

    @Override
    public <T extends Response> T send(Request<T> request) throws NetException {
        ResultSynchronizer.set(request.getRequestId(), request);
        channel.send(request);
        return (T) ResultSynchronizer.get(request.getRequestId(), 5000);
    }

    @Override
    public <T extends Response> Future<T> asyncSend(Request<T> request) throws NetException {
        return null;
    }

    @Override
    public boolean isOpen() {
        return false;
    }

    @Override
    public void close() {
        super.close();
        try {
            channel.close();
        } catch (Exception e) {
            logger.warn("Failed to close channel", e);
        }
    }

    @Override
    public boolean isSSL() {
        return false;
    }

    @Override
    public boolean isTransportable() {
        return isTransportable;
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return channel.getLocalAddress();
    }

    @Override
    public Channel getChannel() {
        return channel;
    }
}
