package rpc;

import com.google.protobuf.Int32Value;
import com.google.protobuf.StringValue;

/**
 * @author xiaoyu
 */
public interface HelloService {
    StringValue sayHiProto(StringValue username, Int32Value age);

    String sayHi(String username, Integer age);
}
