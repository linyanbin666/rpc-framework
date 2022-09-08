package com.horin.rpc.framework.core.transport.netty.server;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.horin.rpc.framework.common.bean.RpcServiceConfig;
import com.horin.rpc.framework.core.provider.DefaultServiceProvider;
import com.horin.rpc.framework.core.provider.ServiceProvider;
import com.horin.rpc.framework.core.transport.netty.codec.RpcMessageDecoder;
import com.horin.rpc.framework.core.transport.netty.codec.RpcMessageEncoder;
import com.horin.rpc.framework.core.util.SingletonFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyServer {

  public static final int PORT = 7777;

  private final ServiceProvider serviceProvider = SingletonFactory.getInstance(
      DefaultServiceProvider.class);

  public void registerService(RpcServiceConfig rpcServiceConfig) {
    serviceProvider.publishService(rpcServiceConfig);
  }

  public void unRegisterService(RpcServiceConfig rpcServiceConfig) {
    serviceProvider.removeService(rpcServiceConfig);
  }

  public void start() {
    NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
    NioEventLoopGroup workerGroup = new NioEventLoopGroup();
    DefaultEventExecutorGroup serviceExecutorGroup = new DefaultEventExecutorGroup(
        Runtime.getRuntime().availableProcessors() * 2,
        new ThreadFactoryBuilder()
            .setNameFormat("service-handler-%d")
            .build()
    );
    try {
      new ServerBootstrap()
          .group(bossGroup, workerGroup)
          .channel(NioServerSocketChannel.class)
          .handler(new LoggingHandler(LogLevel.INFO))
          .childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
              ch.pipeline()
                  .addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS))
                  .addLast(new RpcMessageEncoder())
                  .addLast(new RpcMessageDecoder())
                  .addLast(serviceExecutorGroup, new NettyServerHandler());
            }
          })
          .bind(PORT).sync().channel().closeFuture().sync();
    } catch (InterruptedException e) {
      log.error("Start server failed!", e);
    } finally {
      bossGroup.shutdownGracefully();
      workerGroup.shutdownGracefully();
      serviceExecutorGroup.shutdownGracefully();
    }
  }

}
