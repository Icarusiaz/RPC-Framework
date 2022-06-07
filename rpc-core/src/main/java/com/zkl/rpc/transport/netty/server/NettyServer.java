package com.zkl.rpc.transport.netty.server;

import com.zkl.rpc.codec.CommonDecoder;
import com.zkl.rpc.codec.CommonEncoder;
import com.zkl.rpc.enumeration.RpcError;
import com.zkl.rpc.exception.RpcException;
import com.zkl.rpc.serializer.CommonSerializer;
import com.zkl.rpc.serializer.HessianSerializer;
import com.zkl.rpc.serializer.JsonSerializer;
import com.zkl.rpc.serializer.KryoSerializer;
import com.zkl.rpc.transport.RpcServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  对于服务端而言，有两种通道需要处理， 一种是ServerSocketChannel：用于处理用户连接的accept操作，
 *  另一种是SocketChannel，表示对应客户端连接
 */

public class NettyServer implements RpcServer {
    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);

    private CommonSerializer serializer;

    @Override
    public void start(int port) {
        if(serializer == null) {
            logger.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }

        //bossGroup线程组：处理连接
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        //workerGroup：处理读写
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            //服务端启动助手
            ServerBootstrap serverBootstrap=new ServerBootstrap();
            //设置bossGroup、workerGroup线程组
            serverBootstrap.group(workerGroup, workerGroup)
                    //设置服务端通道实现为NIO
                    .channel(NioServerSocketChannel.class)
                    //handler在初始化时就会执行,设置主通道的处理器， 对于服务端而言就是ServerSocketChannel，
                    // 也就是用来处理Acceptor的操作
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .option(ChannelOption.SO_BACKLOG,256)
                    .option(ChannelOption.SO_KEEPALIVE,true)
                    //表示处理或配置服务端接收到的对应客户端连接的SocketChannel通道
                    .childOption(ChannelOption.TCP_NODELAY,true)
                    //创建一个通道初始化对象
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline=ch.pipeline();
                            pipeline.addLast(new CommonEncoder(serializer))
                                    .addLast(new CommonDecoder())
                                    .addLast(new NettyServerHandler());
                            //向pipeline中添加自定义处理handler
                        }
                    });
            //启动服务端并绑定端口，同步
            ChannelFuture future=serverBootstrap.bind(port).sync();
            //关闭通道
            future.channel().closeFuture().sync();
        }catch(InterruptedException e){
            logger.error("启动服务器时有错误发生: ", e);
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    @Override
    public void setSerializer(CommonSerializer serializer) {
        this.serializer=serializer;
    }
}
