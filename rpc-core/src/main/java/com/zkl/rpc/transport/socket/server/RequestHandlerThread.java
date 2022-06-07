package com.zkl.rpc.transport.socket.server;

import com.zkl.rpc.entity.RpcRequest;
import com.zkl.rpc.entity.RpcResponse;
import com.zkl.rpc.handler.RequestHandler;
import com.zkl.rpc.registry.ServiceRegistry;
import com.zkl.rpc.serializer.CommonSerializer;
import com.zkl.rpc.transport.socket.util.ObjectReader;
import com.zkl.rpc.transport.socket.util.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;

public class RequestHandlerThread implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandlerThread.class);

    private Socket socket;
    private RequestHandler requestHandler;
    private ServiceRegistry serviceRegistry;
    private CommonSerializer serializer;

    public RequestHandlerThread(Socket socket, RequestHandler requestHandler,
                                ServiceRegistry serviceRegistry,CommonSerializer serializer) {
        this.socket = socket;
        this.requestHandler = requestHandler;
        this.serviceRegistry = serviceRegistry;
        this.serializer=serializer;
    }

    @Override
    public void run() {
        try (InputStream inputStream = socket.getInputStream();
             OutputStream outputStream=socket.getOutputStream();) {
            RpcRequest rpcRequest = (RpcRequest) ObjectReader.readObject(inputStream);

            String interfaceName = rpcRequest.getInterfaceName();
            Object service = serviceRegistry.getService(interfaceName);
            //返回String类型
            Object result = requestHandler.handle(rpcRequest, service);

            //返回 RpcResponse<String>; (statusCode,message,data);
            RpcResponse<Object> response=RpcResponse.success(result,rpcRequest.getRequestId());
            ObjectWriter.writeObject(outputStream,response,serializer);
        } catch (IOException e) {
            logger.error("调用或发送时有错误发生：", e);
        }
    }
}
