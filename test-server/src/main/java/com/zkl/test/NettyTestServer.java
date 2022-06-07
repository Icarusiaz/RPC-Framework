package com.zkl.test;

import com.zkl.rpc.api.HelloService;
import com.zkl.rpc.registry.DefaultServiceRegistry;
import com.zkl.rpc.registry.ServiceRegistry;
import com.zkl.rpc.serializer.HessianSerializer;
import com.zkl.rpc.serializer.KryoSerializer;
import com.zkl.rpc.transport.netty.server.NettyServer;

public class NettyTestServer {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        ServiceRegistry registry = new DefaultServiceRegistry();
        registry.register(helloService);
        NettyServer server = new NettyServer();
        server.setSerializer(new KryoSerializer());
        server.start(9999);
    }
}
