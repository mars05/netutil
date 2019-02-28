package serialization;

import com.github.m5.netutil.rpc.proto.YrpcProtos;
import com.github.m5.netutil.rpc.thrift.YrpcRequest;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TCompactProtocol;
import rpc.HelloService;

/**
 * @author xiaoyu
 */
public class ThriftTest {
    public static void main(String[] args) throws Exception {


        YrpcRequest request = new YrpcRequest();
        request.setRequest_id("20181105150030");
        request.setInterface_name(HelloService.class.getName());
        request.setMethod_name("sayHiJson");

        TSerializer serializer = new TSerializer(new TCompactProtocol.Factory());
        TDeserializer deserializer = new TDeserializer(new TCompactProtocol.Factory());
        YrpcRequest obj = new YrpcRequest();
        deserializer.deserialize(obj, serializer.serialize(request));
        System.out.println(obj);
        System.out.println(serializer.serialize(request).length);


        YrpcProtos.YrpcRequest r = YrpcProtos.YrpcRequest.newBuilder().setRequestId("20181105150030")
                .setInterfaceName(HelloService.class.getName())
                .setMethodName("sayHiJson").build();
        System.out.println(r.getSerializedSize());

    }
}
