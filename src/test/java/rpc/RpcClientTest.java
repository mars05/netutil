package rpc;

import ch.qos.logback.classic.Level;
import com.github.m5.netutil.rpc.ServiceProxyFactory;
import com.github.m5.netutil.rpc.YrpcClient;
import com.github.m5.netutil.util.LogLevelUtils;
import com.github.m5.netutil.util.SSLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.IntStream;

/**
 * @author xiaoyu
 */
public class RpcClientTest {
    private static final Logger LOG = LoggerFactory.getLogger(RpcClientTest.class);

    public static void main(String[] args) throws Exception {
        LogLevelUtils.setRootLevel(Level.INFO);

        //定义客户端
        YrpcClient yrpcClient = new YrpcClient("127.0.0.1", 1111, SSLUtils.createSSLContext());

        //定义服务代理工厂，用于生成RPC服务消费者
        ServiceProxyFactory serviceProxyFactory = new ServiceProxyFactory(yrpcClient);
        //生成RPC服务消费者
        HelloService helloService = serviceProxyFactory.newServiceProxy(HelloService.class);
        GreetingService greetingService = serviceProxyFactory.newServiceProxy(GreetingService.class);

        IntStream.range(0, 100).parallel().forEach(value -> {
            String rs = helloService.sayHi("张三", 123);
            greetingService.a("a李四");
            String b = greetingService.b("b王五");
            String c = greetingService.c();
            greetingService.d();
            System.out.println(rs + "," + b + "," + c);
        });
    }
}
