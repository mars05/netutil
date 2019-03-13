netutil
========================

一个网络相关的工具包，对Netty进行了一些抽象封装，目前实现了简易的rpc调用和websocket服务端，以后也会陆续添加一些有趣的功能。


## 相关用例连接: 
### 服务端代码:

```java
public class RpcServerTest {
    public static void main(String[] args) {

        ServerConfig config = new ServerConfig();
        config.addService(HelloService.class.getName(), new HelloServiceImpl());
        config.addService(GreetingService.class.getName(), new GreetingServiceImpl());
        YrpcServer yrpcServer = new YrpcServer(1111, config);
        System.out.println("启动结果：" + yrpcServer.isOpen());
    }
}
```