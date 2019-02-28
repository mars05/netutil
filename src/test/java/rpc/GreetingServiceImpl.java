package rpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author xiaoyu
 */
public class GreetingServiceImpl implements GreetingService {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void a(String a) {
        logger.info(a);
    }

    @Override
    public String b(String b) {
        logger.info(b);
        return "ack: " + b;
    }

    @Override
    public String c() {
        logger.info("GreetingServiceImpl.c");
        return "ack: GreetingServiceImpl.c";
    }

    @Override
    public void d() {
        logger.info("GreetingServiceImpl.d");
    }

}
