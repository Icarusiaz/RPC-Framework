package com.zkl.test;

import com.zkl.rpc.api.HelloObject;
import com.zkl.rpc.api.HelloService;
import com.zkl.rpc.transport.RpcClientProxy;

public class TestClient {
    public static void main(String[] args) {
        RpcClientProxy proxy = new RpcClientProxy("127.0.0.1", 9000);
        HelloService helloService = proxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(12, "This is a message");
        String res = helloService.hello(object);
        System.out.println(res);
    }
}
