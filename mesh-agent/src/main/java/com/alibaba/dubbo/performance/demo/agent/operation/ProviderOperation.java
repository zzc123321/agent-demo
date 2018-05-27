package com.alibaba.dubbo.performance.demo.agent.operation;

import com.alibaba.dubbo.performance.demo.agent.dubbo.RpcClient;
import com.alibaba.dubbo.performance.demo.agent.registry.EtcdRegistry;
import com.alibaba.dubbo.performance.demo.agent.registry.IRegistry;

public class ProviderOperation implements Operatable {
    public static final ProviderOperation INSTANCE = new ProviderOperation();
    private IRegistry registry = new EtcdRegistry(System.getProperty("etcd.url"));
    private RpcClient rpcClient = new RpcClient(registry);

    public static ProviderOperation getInstance() {
        return INSTANCE;
    }

    @Override
    public Object operation(String interfaceName, String method, String parameterTypesString, String parameter) throws Exception {
        return provider(interfaceName, method, parameterTypesString, parameter);
    }

    public byte[] provider(String interfaceName, String method, String parameterTypesString, String parameter) throws Exception {

        Object result = rpcClient.invoke(interfaceName, method, parameterTypesString, parameter);
        return (byte[]) result;
    }
}
