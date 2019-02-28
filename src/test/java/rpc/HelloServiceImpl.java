package rpc;

import com.google.protobuf.Int32Value;
import com.google.protobuf.StringValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author xiaoyu
 */
public class HelloServiceImpl implements HelloService {
    private static final Logger logger = LoggerFactory.getLogger(HelloServiceImpl.class);

    @Override
    public StringValue sayHiProto(StringValue username, Int32Value age) {
//        logger.info("name: {}, age: {}", username.getValue(), age.getValue());
        return StringValue.newBuilder().setValue("hi").build();
    }

    @Override
    public String sayHi(String username, Integer age) {
        logger.info("name: {}, age: {}", username, age);
        return "hi: " + username;
    }
}
