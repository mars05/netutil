package com.github.m5.netutil.handler;

import com.github.m5.netutil.channel.Channel;
import com.github.m5.netutil.exception.RemoteException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * @author xiaoyu
 */
public class SimpleServerHandler extends AbstractNettyServerHandler {
    private static final Logger logger = LoggerFactory.getLogger(SimpleServerHandler.class);

    public SimpleServerHandler() {
    }

    @Override
    public ExecutorService getExecutorService() {
        return null;
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
        Map<String, Channel> channelMap = getChannelMap();
        Collection<Channel> channels = channelMap.values();
        for (Channel ch : channels) {
            if (ch != channel) {
                ch.send(message);
            }
        }
    }

    @Override
    public void caught(Channel channel, Throwable cause) throws RemoteException {
        logger.error("" + channel, cause);
    }
}
