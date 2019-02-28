package com.github.m5.netutil.rpc;

import com.github.m5.netutil.request.Request;
import io.netty.util.internal.StringUtil;

import java.util.UUID;

/**
 * @author xiaoyu
 */
public class YrpcRequest implements Request<YrpcResponse> {
    private String requestId;
    private String group;
    private String version;
    private String interfaceName;
    private String methodName;
    private Object[] params;

    @Override
    public String getRequestId() {
        return StringUtil.isNullOrEmpty(requestId) ? requestId = UUID.randomUUID().toString() : requestId;
    }

    @Override
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }
}
