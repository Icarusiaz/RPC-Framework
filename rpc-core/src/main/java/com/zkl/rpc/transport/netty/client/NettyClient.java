package com.zkl.rpc.transport.netty.client;

import com.zkl.rpc.codec.CommonDecoder;
import com.zkl.rpc.codec.CommonEncoder;
import com.zkl.rpc.entity.RpcRequest;
import com.zkl.rpc.entity.RpcResponse;
import com.zkl.rpc.enumeration.RpcError;
import com.zkl.rpc.exception.RpcException;
import com.zkl.rpc.serializer.CommonSerializer;
import com.zkl.rpc.serializer.HessianSerializer;
import com.zkl.rpc.serializer.JsonSerializer;
import com.zkl.rpc.serializer.KryoSerializer;
import com.zkl.rpc.transport.RpcClient;
import com.zkl.rpc.transport.netty.server.NettyServerHandler;
import com.zkl.rpc.util.RpcMessageChecker;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicReference;

public class NettyClient implements RpcClient {
    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);

    private String host;
    private int port;
    private static final Bootstrap bootstrap;

    private CommonSerializer serializer;

    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    static {
        EventLoopGroup group=new NioEventLoopGroup();
        bootstrap =new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE,true);
    }

    @Override
    public Object sendRequest(RpcRequest rpcRequest) {
        if(serializer==null){
            logger.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        AtomicReference<Object> result = new AtomicReference<>(null);
        try {
            Channel channel = ChannelProvider.get(new InetSocketAddress(host, port), serializer);
            if(channel.isActive()) {
                channel.writeAndFlush(rpcRequest).addListener(future1 -> {
                    if(future1.isSuccess()) {
                        logger.info(String.format("客户端发送消息: %s", rpcRequest.toString()));
                    } else {
                        logger.error("发送消息时有错误发生: ", future1.cause());
                    }
                });

                //阻塞等待，直到Channel关闭
                channel.closeFuture().sync();
                //取出 rpcResponse
                AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse"+rpcRequest.getRequestId());
                RpcResponse rpcResponse = channel.attr(key).get();
                RpcMessageChecker.check(rpcRequest,rpcResponse);
                result.set(rpcResponse.getData());
            }
            else{
                System.exit(0);
            }
        } catch (InterruptedException e) {
            logger.error("发送消息时有错误发生: ", e);
        }
        return result.get();
    }

    @Override
    public void setSerializer(CommonSerializer serializer) {
        this.serializer=serializer;
    }
}
