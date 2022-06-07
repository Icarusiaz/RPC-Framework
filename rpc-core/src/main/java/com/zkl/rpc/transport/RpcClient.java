package com.zkl.rpc.transport;

import com.zkl.rpc.entity.RpcRequest;
import com.zkl.rpc.serializer.CommonSerializer;

public interface RpcClient {
    Object sendRequest(RpcRequest rpcRequest);
    void setSerializer(CommonSerializer serializer);
}
