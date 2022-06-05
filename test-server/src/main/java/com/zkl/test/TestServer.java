package com.zkl.test;

import com.zkl.rpc.api.HelloService;
import com.zkl.rpc.registry.DefaultServiceRegistry;
import com.zkl.rpc.registry.ServiceRegistry;
import com.zkl.rpc.transport.RpcServer;

public class TestServer {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        ServiceRegistry serviceRegistry = new DefaultServiceRegistry();
        serviceRegistry.register(helloService);
        RpcServer rpcServer = new RpcServer(serviceRegistry);
        rpcServer.register(9000);
    }
}
