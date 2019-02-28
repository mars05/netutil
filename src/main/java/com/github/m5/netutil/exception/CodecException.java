package com.github.m5.netutil.exception;

/**
 * @author xiaoyu
 */
public class CodecException extends Exception {
    private static final long serialVersionUID = -1784407824628511433L;

    private String requestId;

    /**
     * CodecException
     */
    public CodecException() {
    }

    /**
     * CodecException
     */
    public CodecException(String message) {
        super(message);
    }

    /**
     * CodecException
     */
    public CodecException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * CodecException
     */
    public CodecException(String requestId, String message) {
        super(message);
        this.requestId = requestId;
    }

    /**
     * CodecException
     */
    public CodecException(String requestId, String message, Throwable cause) {
        super(message, cause);
        this.requestId = requestId;
    }

    /**
     * CodecException
     */
    public CodecException(Throwable cause) {
        super(cause);
    }

    public String getRequestId() {
        return requestId;
    }
}
