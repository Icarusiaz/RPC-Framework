package com.zkl.test;

import com.zkl.rpc.api.HelloService;
import com.zkl.rpc.registry.DefaultServiceRegistry;
import com.zkl.rpc.registry.ServiceRegistry;
import com.zkl.rpc.transport.socket.server.SocketServer;

public class SocketTestServer {
    public static void main(String[] args) {
        //注册服务
        HelloService helloService = new HelloServiceImpl();
        //注册表
        ServiceRegistry serviceRegistry = new DefaultServiceRegistry();
        serviceRegistry.register(helloService);

        SocketServer rpcServer = new SocketServer(serviceRegistry);
        rpcServer.start(9000);
    }
}
