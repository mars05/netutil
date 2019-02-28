package com.github.m5.netutil.server;

import com.github.m5.netutil.channel.Channel;
import com.github.m5.netutil.transport.Transport;

import java.util.Map;

/**
 * @author xiaoyu
 */
public interface Server extends Transport {

    /**
     * getClientChannelMap
     *
     * @return ip:port,channel
     */
    Map<String, Channel> getClientChannelMap();

}
