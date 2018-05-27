package com.alibaba.dubbo.performance.demo.agent.operation;

public interface Operatable {
    Object operation(String interfaceName, String method, String parameterTypesString, String parameter) throws Exception;
}
