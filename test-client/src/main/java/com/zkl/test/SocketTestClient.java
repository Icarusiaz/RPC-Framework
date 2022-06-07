package com.zkl.test;

import com.zkl.rpc.api.HelloObject;
import com.zkl.rpc.api.HelloService;
import com.zkl.rpc.serializer.KryoSerializer;
import com.zkl.rpc.transport.RpcClientProxy;
import com.zkl.rpc.transport.socket.client.SocketClient;

public class SocketTestClient {
    public static void main(String[] args) {
        //socket方式
        SocketClient client=new SocketClient("127.0.0.1", 9000);
        client.setSerializer(new KryoSerializer());
        RpcClientProxy proxy = new RpcClientProxy(client);

        HelloService helloService = proxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(12, "This is a message");
        //被代理
        String res = helloService.hello(object);

        System.out.println(res);
    }
}
