package com.zkl.rpc.transport;

import com.zkl.rpc.serializer.CommonSerializer;

public interface RpcServer {
    void start(int port);
    void setSerializer(CommonSerializer serializer);
}
