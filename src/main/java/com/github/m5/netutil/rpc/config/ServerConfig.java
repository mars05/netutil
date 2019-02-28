package com.github.m5.netutil.rpc.config;

import io.netty.util.internal.StringUtil;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author xiaoyu
 */
public class ServerConfig {
    private ConcurrentMap<String, Object> interfaceImplMap = new ConcurrentHashMap<>();

    public void addService(String interfaceName, Object interfaceImpl) {
        this.addService(interfaceName, interfaceImpl, null, null);
    }

    public void addService(String interfaceName, Object interfaceImpl, String group, String version) {
        if (StringUtil.isNullOrEmpty(group)) {
            group = "";
        }
        if (StringUtil.isNullOrEmpty(version)) {
            version = "";
        }
        interfaceImplMap.put(interfaceName + "#" + group + "#" + version, interfaceImpl);
    }

    public Object getService(String interfaceName) {
        return this.getService(interfaceName, null, null);
    }

    public Object getService(String interfaceName, String group, String version) {
        if (StringUtil.isNullOrEmpty(group)) {
            group = "";
        }
        if (StringUtil.isNullOrEmpty(version)) {
            version = "";
        }
        return interfaceImplMap.get(interfaceName + "#" + group + "#" + version);
    }
}
