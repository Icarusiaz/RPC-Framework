package com.zkl.rpc.transport.socket.server;

import com.zkl.rpc.enumeration.RpcError;
import com.zkl.rpc.exception.RpcException;
import com.zkl.rpc.handler.RequestHandler;
import com.zkl.rpc.registry.ServiceRegistry;
import com.zkl.rpc.serializer.CommonSerializer;
import com.zkl.rpc.transport.RpcServer;
import com.zkl.rpc.util.ThreadPoolFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class SocketServer implements RpcServer {
    private static final Logger logger = LoggerFactory.getLogger(SocketServer.class);

    private final ExecutorService threadPool;
    private RequestHandler requestHandler = new RequestHandler();
    private final ServiceRegistry serviceRegistry;
    private CommonSerializer serializer;

    public SocketServer(ServiceRegistry serviceRegistry) {
        this.serviceRegistry=serviceRegistry;
        threadPool= ThreadPoolFactory.createDefaultThreadPool("socket-rpc-server");
    }

    @Override
    public void start(int port) {
        if(serializer == null) {
            logger.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("服务器正在启动...");
            Socket socket;
            while((socket = serverSocket.accept()) != null) {
                logger.info("消费者连接: {}:{}" + socket.getInetAddress(),socket.getPort());
                threadPool.execute(new RequestHandlerThread(socket, requestHandler, serviceRegistry,serializer));
            }
            threadPool.shutdown();
        } catch (IOException e) {
            logger.error("服务器启动时时有错误发生：", e);
        }
    }

    @Override
    public void setSerializer(CommonSerializer serializer) {
        this.serializer=serializer;
    }
}
