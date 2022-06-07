package com.zkl.rpc.transport;

import com.zkl.rpc.entity.RpcRequest;

public interface RpcClient {
    Object sendRequest(RpcRequest rpcRequest);
}
