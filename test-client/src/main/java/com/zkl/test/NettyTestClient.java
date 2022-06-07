package com.zkl.test;

import com.zkl.rpc.api.HelloObject;
import com.zkl.rpc.api.HelloService;
import com.zkl.rpc.serializer.HessianSerializer;
import com.zkl.rpc.transport.RpcClient;
import com.zkl.rpc.transport.RpcClientProxy;
import com.zkl.rpc.transport.netty.client.NettyClient;

public class NettyTestClient {
    public static void main(String[] args) {
        RpcClient client=new NettyClient("127.0.0.1", 9999);
        client.setSerializer(new HessianSerializer());
        RpcClientProxy rpcClientProxy=new RpcClientProxy(client);
        HelloService helloService=rpcClientProxy.getProxy(HelloService.class);
        HelloObject object=new HelloObject(12, "This is a message");
        String res = helloService.hello(object);
        System.out.println(res);
    }
}
