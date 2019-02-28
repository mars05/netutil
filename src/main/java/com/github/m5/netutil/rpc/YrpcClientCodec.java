package com.github.m5.netutil.rpc;

import com.alibaba.fastjson.JSON;
import com.github.m5.netutil.codec.Codec;
import com.github.m5.netutil.request.Request;
import com.github.m5.netutil.rpc.proto.YrpcProtos;
import com.github.m5.netutil.util.ResultSynchronizer;
import com.github.m5.netutil.util.SerializationType;
import com.github.m5.netutil.util.SerializationUtils;
import com.google.protobuf.ByteString;
import com.google.protobuf.MessageLite;
import com.google.protobuf.MessageLiteOrBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.CombinedChannelDuplexHandler;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.internal.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.List;

/**
 * @author xiaoyu
 */
final class YrpcClientCodec extends CombinedChannelDuplexHandler<ByteToMessageDecoder, MessageToByteEncoder> implements Codec {
    private static final Logger logger = LoggerFactory.getLogger(YrpcClientCodec.class);
    private SerializationType serializationType;

    public YrpcClientCodec(SerializationType serializationType) {
        this.serializationType = serializationType;
        init(new ResponseDecoder(), new RequestEncoder());
    }

    @Override
    public byte[] encode(Object message) {
        if (null == message) {
            return null;
        }
        if (!(message instanceof YrpcRequest)) {
            throw new UnsupportedOperationException("Unsupported type of " + message.getClass());
        }
        YrpcRequest req = (YrpcRequest) message;
        byte[] bytes;
        switch (serializationType) {
            case PROTO:
                Object[] params = req.getParams();
                if (params != null) {
                    for (Object param : params) {
                        if (!(param instanceof MessageLiteOrBuilder)) {
                            throw new UnsupportedOperationException("All the params must be MessageLiteOrBuilder");
                        }
                    }
                }

                YrpcProtos.YrpcRequest.Builder builder = YrpcProtos.YrpcRequest.newBuilder();
                if (!StringUtil.isNullOrEmpty(req.getRequestId())) {
                    builder.setRequestId(req.getRequestId());
                }
                if (!StringUtil.isNullOrEmpty(req.getGroup())) {
                    builder.setGroup(req.getGroup());
                }
                if (!StringUtil.isNullOrEmpty(req.getVersion())) {
                    builder.setVersion(req.getVersion());
                }
                if (!StringUtil.isNullOrEmpty(req.getInterfaceName())) {
                    builder.setInterfaceName(req.getInterfaceName());
                }
                if (!StringUtil.isNullOrEmpty(req.getMethodName())) {
                    builder.setMethodName(req.getMethodName());
                }
                if (params != null) {
                    for (Object param : params) {
                        if (param instanceof MessageLite) {
                            builder.addParams(ByteString.copyFrom(((MessageLite) param).toByteArray()));
                        } else if (param instanceof MessageLite.Builder) {
                            builder.addParams(ByteString.copyFrom(((MessageLite.Builder) param).build().toByteArray()));
                        }
                    }
                }
                YrpcProtos.YrpcRequest req1 = builder.build();
                bytes = req1.toByteArray();
                break;
            case THRIFT:
                throw new UnsupportedOperationException();
            case JSON:
                bytes = JSON.toJSONString(req).getBytes();
                break;
            case JAVA:
                bytes = SerializationUtils.serialize(req);
                break;

            default:
                throw new UnsupportedOperationException();
        }
        return bytes;
    }

    @Override
    public Object decode(byte[] bytes) {
        return null;
    }

    private final class ResponseDecoder extends ByteToMessageDecoder {
        private static final int HEAD_BYTE_COUNT = 5;
        private volatile boolean isEncode;
        private volatile int encodeLength;
        private volatile SerializationType serializationType;

        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
            if (logger.isDebugEnabled()) {
                logger.debug("-----decode before----- in: {},out: {},inObj: {},outObj: {}", in, out, System.identityHashCode(in), System.identityHashCode(out));
            }
            while (in.readableBytes() > 0) {
                if (isEncode) {
                    if (in.readableBytes() >= encodeLength) {
                        ByteBuf byteBuf = in.readBytes(encodeLength);
                        YrpcResponse resp;
                        try {
                            switch (serializationType) {
                                case PROTO:
                                    YrpcProtos.YrpcResponse resp1 = YrpcProtos.YrpcResponse.parseFrom(byteBuf.nioBuffer());
                                    resp = new YrpcResponse();
                                    YrpcRequest request = null;
                                    if (!StringUtil.isNullOrEmpty(resp1.getRequestId())) {
                                        resp.setRequestId(resp1.getRequestId());
                                        Request req = ResultSynchronizer.get(resp.getRequestId());
                                        if (req instanceof YrpcRequest) {
                                            request = (YrpcRequest) req;
                                        }
                                    }
                                    if (!StringUtil.isNullOrEmpty(resp1.getErrMsg())) {
                                        resp.setErrMsg(resp1.getErrMsg());
                                    }
                                    if (request != null && StringUtil.isNullOrEmpty(resp.getErrMsg())) {
                                        Class<?> aClass = Class.forName(request.getInterfaceName());
                                        Class<?>[] paramsClass = null;
                                        Object[] args = request.getParams();
                                        if (args != null) {
                                            paramsClass = new Class[args.length];
                                            for (int i = 0; i < args.length; i++) {
                                                paramsClass[i] = args[i].getClass();
                                            }
                                        }

                                        Method method = aClass.getMethod(request.getMethodName(), paramsClass);
                                        Class<?> returnClass = method.getReturnType();
                                        ByteString resultByte = resp1.getResult();
                                        if (MessageLite.class.isAssignableFrom(returnClass)) {
                                            resp.setResult(((MessageLiteOrBuilder) returnClass
                                                    .getMethod("newBuilder").invoke(null))
                                                    .getDefaultInstanceForType().getParserForType()
                                                    .parseFrom(resultByte));
                                        } else if (MessageLite.Builder.class.isAssignableFrom(returnClass)) {
                                            Constructor<?> constructor = returnClass.getDeclaredConstructor();
                                            constructor.setAccessible(true);
                                            resp.setResult(((MessageLite.Builder) constructor.newInstance()).mergeFrom(resultByte));
                                        }
                                    }

                                    break;
                                case THRIFT:
                                    throw new UnsupportedOperationException();
                                case JSON:
                                    resp = JSON.parseObject(byteBuf.toString(Charset.defaultCharset()), YrpcResponse.class);
                                    break;
                                case JAVA:
                                    byte[] bytes = new byte[byteBuf.readableBytes()];
                                    byteBuf.readBytes(bytes);
                                    resp = (YrpcResponse) SerializationUtils.deserialize(bytes);
                                    break;
                                default:
                                    throw new UnsupportedOperationException();
                            }
                        } finally {
                            serializationType = null;
                            isEncode = false;
                            encodeLength = 0;
                            byteBuf.release();
                        }
                        if (!StringUtil.isNullOrEmpty(resp.getRequestId())) {
                            out.add(resp);
                        }
                        continue;
                    }
                    break;
                } else {
                    if (in.readableBytes() >= HEAD_BYTE_COUNT) {
                        byte typeBit = in.readByte();
                        SerializationType type = SerializationType.valueOf(typeBit);
                        if (type == null) {
                            in.resetReaderIndex();
                            in.resetWriterIndex();
                            throw new UnsupportedOperationException("Unsupported protocol");
                        }
                        int lenValue = 0;
                        lenValue += ((in.readByte() & 0xff) << 24);
                        lenValue += ((in.readByte() & 0xff) << 16);
                        lenValue += ((in.readByte() & 0xff) << 8);
                        lenValue += (in.readByte() & 0xff);

                        serializationType = type;
                        isEncode = true;
                        encodeLength = lenValue;
                        continue;
                    }
                    break;
                }
            }
            if (logger.isDebugEnabled() && !out.isEmpty()) {
                logger.debug("-----decode after----- in: {},out: {},inObj: {},outObj: {}", in, out.size(), System.identityHashCode(in), System.identityHashCode(out));
            }
        }
    }

    private final class RequestEncoder extends MessageToByteEncoder {
        private YrpcClientCodec codec = YrpcClientCodec.this;

        @Override
        protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
            if (logger.isDebugEnabled()) {
                logger.debug("-----encode before----- msg: {},out: {},msgObj: {},outObj: {}", msg, out, System.identityHashCode(msg), System.identityHashCode(out));
            }
            if (!(msg instanceof Request)) {
                throw new IllegalArgumentException("The message type must be Request.class");
            }
            ByteBuf buf = ctx.alloc().buffer();
            try {
                byte[] bytes = codec.encode(msg);
                int length = bytes.length;
                buf.writeByte(serializationType.bitValue);
                buf.writeByte((length >> 24) & 0xff);
                buf.writeByte((length >> 16) & 0xff);
                buf.writeByte((length >> 8) & 0xff);
                buf.writeByte(length & 0xff);
                out.writeBytes(buf.writeBytes(bytes));
            } finally {
                buf.release();
            }
            if (logger.isDebugEnabled()) {
                logger.debug("-----encode after----- msg: {},out: {},msgObj: {},outObj: {}", msg, out, System.identityHashCode(msg), System.identityHashCode(out));
            }
        }

    }
}
