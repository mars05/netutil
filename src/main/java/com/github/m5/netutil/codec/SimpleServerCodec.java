package com.github.m5.netutil.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.CombinedChannelDuplexHandler;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.List;

/**
 * @author xiaoyu
 */
public final class SimpleServerCodec extends CombinedChannelDuplexHandler<ByteToMessageDecoder, MessageToByteEncoder> implements Codec {
    private static final Logger logger = LoggerFactory.getLogger(SimpleServerCodec.class);

    public SimpleServerCodec() {
        init(new RequestDecoder(), new ResponseEncoder());
    }

    @Override
    public byte[] encode(Object message) {
        return new byte[0];
    }

    @Override
    public Object decode(byte[] bytes) {
        return null;
    }

    protected static final class RequestDecoder extends ByteToMessageDecoder {
        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
            if (logger.isDebugEnabled()) {
                logger.info("-----decode before----- in: {},out: {},inObj: {},outObj: {}", in, out, System.identityHashCode(in), System.identityHashCode(out));
            }
            int readableBytes = in.readableBytes();
            if (readableBytes > 0) {
                ByteBuf byteBuf = in.readBytes(readableBytes);
                String str = byteBuf.toString(Charset.defaultCharset());
                out.add(str);
            }
            if (logger.isDebugEnabled()) {
                logger.info("-----decode after----- in: {},out: {},inObj: {},outObj: {}", in, out, System.identityHashCode(in), System.identityHashCode(out));
            }
        }
    }

    protected static final class ResponseEncoder extends MessageToByteEncoder {

        @Override
        protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
            if (logger.isDebugEnabled()) {
                logger.info("-----encode before----- msg: {},out: {},inObj: {},outObj: {}", msg, out, System.identityHashCode(msg), System.identityHashCode(out));
            }
            out.writeBytes(((String) msg).getBytes());
            if (logger.isDebugEnabled()) {
                logger.info("-----encode after----- msg: {},out: {},inObj: {},outObj: {}", msg, out, System.identityHashCode(msg), System.identityHashCode(out));
            }
        }
    }
}
