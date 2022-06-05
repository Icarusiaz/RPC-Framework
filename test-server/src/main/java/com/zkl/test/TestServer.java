package com.zkl.test;

import com.zkl.rpc.api.HelloService;
import com.zkl.rpc.transport.RpcServer;

public class TestServer {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        RpcServer rpcServer = new RpcServer();
        rpcServer.register(helloService, 9000);
    }
}
