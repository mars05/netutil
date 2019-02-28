package com.github.m5.netutil.response;

/**
 * @author xiaoyu
 */
public interface ResponseListener<T> {
    void onResponse(T response);
}
