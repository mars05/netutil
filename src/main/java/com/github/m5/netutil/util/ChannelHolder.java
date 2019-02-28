package com.github.m5.netutil.util;

import com.github.m5.netutil.channel.Channel;

import java.net.InetSocketAddress;

/**
 * @author xiaoyu
 */
public class ChannelHolder {
    private String channelName;
    private InetSocketAddress address;
    private Channel channel;

    public ChannelHolder(String channelName, Channel channel) {
        this.channelName = channelName;
        this.channel = channel;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public InetSocketAddress getAddress() {
        return address;
    }

    public void setAddress(InetSocketAddress address) {
        this.address = address;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}
