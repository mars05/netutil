package com.github.m5.netutil.exception;

/**
 * @author xiaoyu
 */
public class NetException extends RuntimeException {
    private static final long serialVersionUID = -7470843932246227046L;

    public NetException() {
    }

    public NetException(String message) {
        super(message);
    }

    public NetException(String message, Throwable cause) {
        super(message, cause);
    }

    public NetException(Throwable cause) {
        super(cause);
    }

}
