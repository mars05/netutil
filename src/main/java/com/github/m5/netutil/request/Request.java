package com.github.m5.netutil.request;

import com.github.m5.netutil.response.Response;

import java.io.Serializable;

/**
 * @author xiaoyu
 */
public interface Request<T extends Response> extends Serializable {
    /**
     * @return
     */
    String getRequestId();

    /**
     * @param requestId
     */
    void setRequestId(String requestId);
}
