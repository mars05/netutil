package com.github.m5.netutil.server;

import com.github.m5.netutil.channel.Channel;
import com.github.m5.netutil.channel.NettyChannel;
import com.github.m5.netutil.util.Constants;
import com.github.m5.netutil.util.NamedThreadFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * @author xiaoyu
 */
public abstract class AbstractNettyServer extends AbstractServer {
    private static final Logger logger = LoggerFactory.getLogger(AbstractNettyServer.class);
    private Channel channel;
    private volatile boolean isTransportable;
    private NioEventLoopGroup bossGroup;
    private NioEventLoopGroup workerGroup;
    private ServerBootstrap bootstrap;

    protected AbstractNettyServer() {
    }

    public AbstractNettyServer(int port) {
        super(port);
    }

    public AbstractNettyServer(String host, int port) {
        super(host, port);
    }

    public AbstractNettyServer(InetSocketAddress bindAddress) {
        super(bindAddress);
    }

    @Override
    protected void doBind() {
        bossGroup = new NioEventLoopGroup(1, new NamedThreadFactory("NettyServerBoss", true));
        workerGroup = new NioEventLoopGroup(Constants.DEFAULT_EVENT_LOOP_THREADS, new NamedThreadFactory("NettyServerWorker", true));

        bootstrap = new ServerBootstrap();
        CountDownLatch cdl = new CountDownLatch(1);
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, Boolean.TRUE)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast("codec", (ChannelHandler) getCodec())
                                .addLast("handler", (ChannelHandler) getHandler());
                    }
                });
        ChannelFuture channelFuture = bootstrap.bind(getLocalAddress()).awaitUninterruptibly();
        if (channelFuture.isSuccess()) {
            isTransportable = true;
            logger.info("{} bind to the {}", AbstractNettyServer.this.getClass().getSimpleName(), getLocalAddress());
            channel = new NettyChannel(channelFuture.channel());
        } else {
            logger.error(channelFuture.cause().getMessage(), channelFuture.cause());
            throw new IllegalStateException(channelFuture.cause().getMessage(), channelFuture.cause());
        }
    }

    @Override
    public void close() {
        super.close();
        try {
            if (channel != null) {
                channel.close();
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            }
        } catch (Exception e) {
            logger.warn("Failed to close channel", e);
        } finally {
            getHandler().getChannelMap().clear();
        }
    }

    @Override
    public boolean isOpen() {
        return channel.isOpen();
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
    public Channel getChannel() {
        return channel;
    }

    @Override
    public Map<String, Channel> getClientChannelMap() {
        return getHandler().getChannelMap();
    }
}
