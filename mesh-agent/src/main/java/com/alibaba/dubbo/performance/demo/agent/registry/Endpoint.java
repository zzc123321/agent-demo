package com.alibaba.dubbo.performance.demo.agent.registry;

public class Endpoint {
    private final String host;
    private final int port;
    private final int performance;

    public Endpoint(String host,int port,int performance){
        this.host = host;
        this.port = port;
        this.performance=performance;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public int getPerformance() {
        return performance;
    }

    public String toString(){
        return host + ":" + port+":"+performance;
    }

    public boolean equals(Object o){
        if (!(o instanceof Endpoint)){
            return false;
        }
        Endpoint other = (Endpoint) o;
        return other.host.equals(this.host) && other.port == this.port&&other.performance==this.performance;
    }

    public int hashCode(){
        return host.hashCode() + port+performance;
    }
}
