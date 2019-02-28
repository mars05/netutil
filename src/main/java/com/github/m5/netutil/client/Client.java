package com.github.m5.netutil.client;

import com.github.m5.netutil.exception.NetException;
import com.github.m5.netutil.request.Request;
import com.github.m5.netutil.response.Response;
import com.github.m5.netutil.transport.Transport;

import java.net.InetSocketAddress;
import java.util.concurrent.Future;

/**
 * @author xiaoyu
 */
public interface Client extends Transport {

    InetSocketAddress getRemoteAddress();

    <T extends Response> T send(Request<T> request) throws NetException;

    <T extends Response> Future<T> asyncSend(Request<T> request) throws NetException;

}




