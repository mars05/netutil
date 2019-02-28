package com.github.m5.netutil.response;

import java.io.Serializable;

/**
 * @author xiaoyu
 */
public interface Response extends Serializable {
    /**
     * getRequestId
     *
     * @return
     */
    String getRequestId();

    /**
     * setRequestId
     *
     * @param requestId
     */
    void setRequestId(String requestId);
}
