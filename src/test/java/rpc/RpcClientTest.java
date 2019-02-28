package rpc;

import ch.qos.logback.classic.Level;
import com.github.m5.netutil.rpc.ServiceProxyFactory;
import com.github.m5.netutil.rpc.YrpcClient;
import com.github.m5.netutil.util.LogLevelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author xiaoyu
 */
public class RpcClientTest {
    private static final Logger LOG = LoggerFactory.getLogger(RpcClientTest.class);

    public static void main(String[] args) throws Exception {
        LogLevelUtils.setRootLevel(Level.INFO);

        ServiceProxyFactory serviceProxyFactory = new ServiceProxyFactory(new YrpcClient("127.0.0.1", 1111));

        ExecutorService executorService = Executors.newFixedThreadPool(50);
        HelloService helloService = serviceProxyFactory.newServiceProxy(HelloService.class);
        GreetingService greetingService = serviceProxyFactory.newServiceProxy(GreetingService.class);

        for (int i = 0; i < 10000; i++) {
            executorService.execute(() -> {
                String rs = helloService.sayHi("张三", 123);
                greetingService.a("a李四");
                String b = greetingService.b("b王五");
                String c = greetingService.c();
                greetingService.d();
                System.out.println(rs + "," + b + "," + c);
            });
        }

    }
}
