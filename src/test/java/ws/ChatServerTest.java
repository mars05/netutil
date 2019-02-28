package ws;

import ch.qos.logback.classic.Level;
import com.github.m5.netutil.util.LogLevelUtils;
import com.github.m5.netutil.ws.WebSocketChannel;
import com.github.m5.netutil.ws.WebSocketRequestHandler;
import com.github.m5.netutil.ws.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

/**
 * @author xiaoyu
 */
public class ChatServerTest {
    private static final Logger LOG = LoggerFactory.getLogger(ChatServerTest.class);

    public static void main(String[] args) {
        LogLevelUtils.setRootLevel(Level.INFO);

        WebSocketServer server = new WebSocketServer(2222, new WebSocketRequestHandler() {
            @Override
            public void onOpen(WebSocketChannel webSocketChannel) {
                LOG.info("建立连接: {},", webSocketChannel, getWebSocketChannels().size());
                getWebSocketChannels().forEach(d -> {
                    if (!webSocketChannel.equals(d)) {
                        d.send("> " + webSocketChannel.getQueryParam("name") + "，加入聊天");
                    }
                });
            }

            @Override
            public void onMessage(WebSocketChannel webSocketChannel, String message) {
                LOG.info("收到消息, {}: {}", webSocketChannel.getQueryParam("name"), message);
                getWebSocketChannels().forEach(d -> {
                    if (!webSocketChannel.equals(d)) {
                        d.send(webSocketChannel.getQueryParam("name") + ": " + message);
                    }
                });
            }

            @Override
            public void onClose(WebSocketChannel webSocketChannel, int code, String reason, boolean remote) {
                LOG.info("关闭连接: {}, code: {}, reason: {}, remote: {}, open: {}", webSocketChannel, code, reason, remote, webSocketChannel.isOpen());
                getWebSocketChannels().forEach(d -> {
                    if (!webSocketChannel.equals(d) && d.isOpen()) {
                        d.send("> " + webSocketChannel.getQueryParam("name") + "，退出聊天");
                    }
                });
            }

            @Override
            public void onError(WebSocketChannel webSocketChannel, Throwable throwable) {
                LOG.error("发生异常: {}", webSocketChannel, throwable);
            }
        });
        System.out.println("启动结果：" + server.isOpen());
        if (new Scanner(System.in).nextLine() != null) {
            System.out.println(server.getClientChannelMap().size());
        }
    }
}
