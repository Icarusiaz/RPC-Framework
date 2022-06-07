package com.zkl.rpc.transport.socket.client;

import com.esotericsoftware.kryo.io.Input;
import com.zkl.rpc.entity.RpcRequest;
import com.zkl.rpc.entity.RpcResponse;
import com.zkl.rpc.enumeration.ResponseCode;
import com.zkl.rpc.enumeration.RpcError;
import com.zkl.rpc.exception.RpcException;
import com.zkl.rpc.serializer.CommonSerializer;
import com.zkl.rpc.transport.RpcClient;
import com.zkl.rpc.transport.socket.util.ObjectReader;
import com.zkl.rpc.transport.socket.util.ObjectWriter;
import com.zkl.rpc.util.RpcMessageChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;

public class SocketClient implements RpcClient {
    private static final Logger logger = LoggerFactory.getLogger(SocketClient.class);

    private final String host;
    private final int port;

    private CommonSerializer serializer;

    public SocketClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public Object sendRequest(RpcRequest rpcRequest) {
        if(serializer == null) {
            logger.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }

        try (Socket socket = new Socket(host, port)) {
//            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
//            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            OutputStream outputStream=socket.getOutputStream();
            InputStream inputStream=socket.getInputStream();

            ObjectWriter.writeObject(outputStream,rpcRequest,serializer);
            Object obj= ObjectReader.readObject(inputStream);
            RpcResponse rpcResponse=(RpcResponse) obj;

//            objectOutputStream.writeObject(rpcRequest);
//            objectOutputStream.flush();
            //接收 RpcResponse<String>
//            RpcResponse rpcResponse = (RpcResponse) objectInputStream.readObject();

            if(rpcResponse == null) {
                logger.error("服务调用失败，service：{}", rpcRequest.getInterfaceName());
                throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE, " service:" + rpcRequest.getInterfaceName());
            }
            if(rpcResponse.getStatusCode() == null || rpcResponse.getStatusCode() != ResponseCode.SUCCESS.getCode()) {
                logger.error("调用服务失败, service: {}, response:{}", rpcRequest.getInterfaceName(), rpcResponse);
                throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE, " service:" + rpcRequest.getInterfaceName());
            }

            RpcMessageChecker.check(rpcRequest,rpcResponse);
            return rpcResponse.getData();
        } catch (IOException e) {
            logger.error("调用时有错误发生：", e);
            throw new RpcException("服务调用失败：", e);
        }
    }

    @Override
    public void setSerializer(CommonSerializer serializer) {
        this.serializer=serializer;
    }
}
