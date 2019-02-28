package com.github.m5.netutil.util;

import com.github.m5.netutil.exception.RemoteException;
import com.github.m5.netutil.request.Request;
import com.github.m5.netutil.response.Response;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author xiaoyu
 */
public class ResultSynchronizer {
    private static final ConcurrentMap<String, Request> reqMap = new ConcurrentHashMap<>();
    private static final ConcurrentMap<String, SynchronousQueue<Response>> respMap = new ConcurrentHashMap<>();

    private ResultSynchronizer() {
    }

    public static void set(String requestId, Request request) {
        reqMap.put(requestId, request);
        respMap.putIfAbsent(requestId, new SynchronousQueue<>());
    }

    public static void set(String requestId, Response response, long timeoutMillis) throws RemoteException {
        SynchronousQueue<Response> queue = respMap.get(requestId);
        if (null != queue) {
            try {
                queue.offer(response, timeoutMillis, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                throw new RemoteException(e.getMessage(), e);
            }
        }
    }

    public static Request get(String requestId) {
        return reqMap.get(requestId);
    }


    public static Response get(String requestId, long timeoutMillis) throws RemoteException {
        try {
            Response response = respMap.get(requestId).poll(timeoutMillis, TimeUnit.MILLISECONDS);
            if (null == response) {
                throw new TimeoutException("Wait result timeout, more than " + timeoutMillis + " ms");
            }
            return response;
        } catch (Exception e) {
            throw new RemoteException(e.getMessage(), e);
        } finally {
            reqMap.remove(requestId);
            respMap.remove(requestId);
        }
    }

}
