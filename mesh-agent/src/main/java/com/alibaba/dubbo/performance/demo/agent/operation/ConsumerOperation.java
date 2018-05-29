package com.alibaba.dubbo.performance.demo.agent.operation;

import com.alibaba.dubbo.performance.demo.agent.registry.Endpoint;
import com.alibaba.dubbo.performance.demo.agent.registry.EtcdRegistry;
import com.alibaba.dubbo.performance.demo.agent.registry.IRegistry;
import com.alibaba.fastjson.JSON;
import okhttp3.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class ConsumerOperation implements Operatable {
    public static final ConsumerOperation INSTANCE = new ConsumerOperation();
    private IRegistry registry = new EtcdRegistry(System.getProperty("etcd.url"));
    private Random random = new Random();
    private List<Endpoint> endpoints = null;
    private Object lock = new Object();
    private OkHttpClient httpClient = new OkHttpClient();

    public static ConsumerOperation getInstance() {
        return INSTANCE;
    }

    @Override
    public Object operation(String interfaceName, String method, String parameterTypesString, String parameter) throws Exception {
        return consumer(interfaceName, method, parameterTypesString, parameter);
    }

    public Integer consumer(String interfaceName, String method, String parameterTypesString, String parameter) throws Exception {

        if (null == endpoints) {
            getEndpoints();
        }

        // 简单的负载均衡，随机取一个
//         Endpoint endpoint = endpoints.get(random.nextInt(endpoints.size()));
        Endpoint endpoint = getEndpointWithWeight();
        String url = "http://" + endpoint.getHost() + ":" + endpoint.getPort();

        RequestBody requestBody = new FormBody.Builder()
                .add("interface", interfaceName)
                .add("method", method)
                .add("parameterTypesString", parameterTypesString)
                .add("parameter", parameter)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            byte[] bytes = response.body().bytes();
            return JSON.parseObject(bytes, Integer.class);
        }
    }

    private synchronized void getEndpoints() throws Exception{
        endpoints = registry.find("com.alibaba.dubbo.performance.demo.provider.IHelloService");
    }

    private Endpoint getEndpointWithWeight() {
        int maxRange = 0;
        Map<Endpoint, Map<String, Integer>> compareMap = new ConcurrentHashMap<>();
        for (Endpoint endpoint : endpoints) {
            Map<String, Integer> numRangeMap = new ConcurrentHashMap<>();
            numRangeMap.put("min", maxRange);
            maxRange = maxRange + endpoint.getPerformance();
            numRangeMap.put("max", maxRange);
            compareMap.put(endpoint, numRangeMap);
        }
        Random rand = new Random();
        int judgeNum = rand.nextInt(maxRange);
        return compareMap.entrySet().stream().
                filter(endpointMapEntry ->
                        endpointMapEntry.getValue().entrySet().stream().filter(minEntry -> "min".equals(minEntry.getKey())).findFirst().get().getValue() <= judgeNum
                                && endpointMapEntry.getValue().entrySet().stream().filter(maxEntry -> "max".equals(maxEntry.getKey())).findFirst().get().getValue() > judgeNum
                ).findFirst().get().getKey();
    }
}
