package rpc;

import ch.qos.logback.classic.Level;
import com.github.m5.netutil.rpc.YrpcServer;
import com.github.m5.netutil.rpc.config.ServerConfig;
import com.github.m5.netutil.util.LogLevelUtils;
import com.github.m5.netutil.util.SSLUtils;

import javax.net.ssl.SSLContext;

/**
 * @author xiaoyu
 */
public class RpcServerTest {
    public static void main(String[] args) {
        LogLevelUtils.setRootLevel(Level.INFO);

        //配置RPC服务提供者
        ServerConfig config = new ServerConfig();
        config.addService(HelloService.class.getName(), new HelloServiceImpl());
        config.addService(GreetingService.class.getName(), new GreetingServiceImpl());

        //按需配置安全证书
        SSLContext ssl = SSLUtils.createSSLContext(ClassLoader.getSystemResourceAsStream("test.pfx"), null, "7hukgn0h");

        YrpcServer yrpcServer;
        if (null == ssl) {
            yrpcServer = new YrpcServer(1111, config);
        } else {
            yrpcServer = new YrpcServer(1111, ssl, config);
        }
        System.out.println("启动结果：" + yrpcServer.isOpen());

    }
}
