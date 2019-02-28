package com.github.m5.netutil.util;

/**
 * @author xiaoyu
 */
public enum SerializationType {
    PROTO((byte) 1),
    THRIFT((byte) 2),
    JSON((byte) 3),
    JAVA((byte) 4);

    public byte bitValue;

    SerializationType(byte bitValue) {
        this.bitValue = bitValue;
    }

    public static SerializationType valueOf(byte bitValue) {
        for (SerializationType type : values()) {
            if (type.bitValue == bitValue) {
                return type;
            }
        }
        return null;
    }
}
