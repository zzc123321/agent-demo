package com.alibaba.dubbo.performance.demo.agent.operation;

public enum OperationType {
    CONSUMER("consumer"),
    PROVIDER("provider");
    private String typeName;

    OperationType(String typeName) {
        this.typeName = typeName;
    }

}
