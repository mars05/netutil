package com.github.m5.netutil.ws;

/**
 * @author xiaoyu
 */
class WebSocketRequest {
    private WebSocketChannel webSocketChannel;
    private String message;

    public WebSocketChannel getWebSocketChannel() {
        return webSocketChannel;
    }

    public void setWebSocketChannel(WebSocketChannel webSocketChannel) {
        this.webSocketChannel = webSocketChannel;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
