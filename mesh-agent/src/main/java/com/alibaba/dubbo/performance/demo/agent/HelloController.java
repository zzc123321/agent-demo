package com.alibaba.dubbo.performance.demo.agent;

import com.alibaba.dubbo.performance.demo.agent.operation.ConsumerOperation;
import com.alibaba.dubbo.performance.demo.agent.operation.Operatable;
import com.alibaba.dubbo.performance.demo.agent.operation.OperationType;
import com.alibaba.dubbo.performance.demo.agent.operation.ProviderOperation;
import com.google.common.collect.ImmutableBiMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HelloController {
    public static final Logger logger=LoggerFactory.getLogger(HelloController.class);

    private Map<OperationType, Operatable> operationMap = ImmutableBiMap.of(OperationType.CONSUMER, ConsumerOperation.getInstance(), OperationType.PROVIDER, ProviderOperation.getInstance());


    @RequestMapping(value = "")
    public Object invoke(@RequestParam("interface") String interfaceName,
                         @RequestParam("method") String method,
                         @RequestParam("parameterTypesString") String parameterTypesString,
                         @RequestParam("parameter") String parameter) throws Exception {
        logger.debug("interface={},method={},parameterTypesString={},parameter={}",interfaceName,method,parameterTypesString,parameter);
        String type = System.getProperty("type");   // 获取type参数
        if (operationMap.get(type) == null) {
            return "Environment variable type is needed to set to provider or consumer.";
        }
        return operationMap.get(type).operation(interfaceName, method, parameterTypesString, parameter);

    }


}
