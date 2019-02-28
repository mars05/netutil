package com.github.m5.netutil.ws;

import com.github.m5.netutil.channel.Channel;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.util.internal.StringUtil;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xiaoyu
 */
public class WebSocketChannel implements Channel {
    private Channel channel;
    private String uri;
    private Map<String, String> params;
    private CloseStatus closeStatus;

    Channel getChannel() {
        return channel;
    }

    void setChannel(Channel channel) {
        this.channel = channel;
    }

    public String getUri() {
        return uri;
    }

    void setUri(String uri) {
        this.uri = uri;
        params = new HashMap<>(1 << 4);
        URI u = URI.create(this.uri);
        String query = u.getQuery();
        if (StringUtil.isNullOrEmpty(query)) {
            return;
        }
        String[] qs = query.split("&");
        for (int i = 0; i < qs.length; i++) {
            String q = qs[i];
            if (!StringUtil.isNullOrEmpty(q)) {
                String[] qq = q.split("=");
                if (qq.length != 2) {
                    break;
                }
                String k = qq[0];
                String v = qq[1];
                params.put(k, v);
            }
        }
    }

    public Map<String, String> getQueryParams() {
        return params;
    }

    public String getQueryParam(String name) {
        return params.get(name);
    }

    void setCloseStatus(CloseStatus closeStatus) {
        this.closeStatus = closeStatus;
    }

    CloseStatus getCloseStatus() {
        return closeStatus;
    }

    @Override
    public boolean isOpen() {
        return channel.isOpen();
    }

    @Override
    public void send(Object message) {
        channel.send(message);
    }

    @Override
    public void sendAndClose(Object message) {
        channel.sendAndClose(message);
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return channel.getLocalAddress();
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return channel.getRemoteAddress();
    }

    @Override
    public void close() {
        this.close(CloseStatus.NORMAL);
    }

    public void close(int code) {
        this.close(code, null);
    }

    public void close(int code, String reason) {
        this.close(new CloseStatus(code, reason));
    }

    public void close(CloseStatus closeStatus) {
        this.closeStatus = closeStatus;
        this.closeStatus.setRemote(false);
        if (this.channel.isOpen()) {
            this.channel.sendAndClose(new CloseWebSocketFrame(closeStatus.getCode(), closeStatus.getReason()));
        } else {
            this.channel.close();
        }
    }

    @Override
    public String toString() {
        return "WebSocketChannel{" +
                "channel=" + channel +
                ", uri='" + uri + '\'' +
                '}';
    }
}
