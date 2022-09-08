package com.horin.rpc.framework.core.transport.netty.client;

import com.horin.rpc.framework.common.bean.RpcMessage;
import com.horin.rpc.framework.common.bean.RpcRequest;
import com.horin.rpc.framework.common.bean.RpcResponse;
import com.horin.rpc.framework.core.constant.RpcConstants;
import com.horin.rpc.framework.core.registry.ServiceDiscovery;
import com.horin.rpc.framework.core.transport.RpcRequestTransport;
import com.horin.rpc.framework.core.transport.netty.codec.RpcMessageDecoder;
import com.horin.rpc.framework.core.transport.netty.codec.RpcMessageEncoder;
import com.horin.rpc.framework.core.util.ExtensionLoader;
import com.horin.rpc.framework.core.util.SingletonFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyClient implements RpcRequestTransport, AutoCloseable {

  private final ServiceDiscovery serviceDiscovery;
  private final ChannelProvider channelProvider;
  private final UnprocessedRequests unprocessedRequests;

  private final EventLoopGroup eventLoopGroup;
  private final Bootstrap bootstrap;

  public NettyClient() {
    serviceDiscovery = ExtensionLoader.getServiceDiscovery();
    channelProvider = SingletonFactory.getInstance(ChannelProvider.class);
    unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
    eventLoopGroup = new NioEventLoopGroup();
    bootstrap = new Bootstrap();
    bootstrap.group(eventLoopGroup)
        .channel(NioSocketChannel.class)
        .handler(new LoggingHandler(LogLevel.INFO))
        .handler(new ChannelInitializer<SocketChannel>() {
          @Override
          protected void initChannel(SocketChannel ch) throws Exception {
            ch.pipeline()
                .addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS))
                .addLast(new RpcMessageDecoder())
                .addLast(new RpcMessageEncoder())
                .addLast(new NettyClientHandler());
          }
        });
  }

  @SneakyThrows
  public Channel connect(InetSocketAddress inetSocketAddress) {
    CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
    bootstrap.connect(inetSocketAddress).addListener(
        (ChannelFutureListener) future -> {
          if (future.isSuccess()) {
            log.info("The client has connected [{}] successful!", inetSocketAddress);
            completableFuture.complete(future.channel());
          } else {
            completableFuture.completeExceptionally(new IllegalStateException("连接失败!"));
          }
        });
    return completableFuture.get();
  }

  @Override
  public Object sendRpcRequest(RpcRequest rpcRequest) {
    CompletableFuture<RpcResponse<Object>> resultFuture = new CompletableFuture<>();
    InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest);
    Channel channel = getChannel(inetSocketAddress);
    if (!channel.isActive()) {
      throw new IllegalStateException("获取到非活跃的通道!");
    }
    unprocessedRequests.put(rpcRequest.getRequestId(), resultFuture);
    RpcMessage rpcMessage = RpcMessage.builder()
        .messageType(RpcConstants.RPC_REQUEST_TYPE)
        .compress(RpcConstants.DEFAULT_COMPRESS_TYPE)
        .codec(RpcConstants.DEFAULT_SERIALIZE_TYPE)
        .data(rpcRequest)
        .build();
    channel.writeAndFlush(rpcMessage).addListener((ChannelFutureListener) feature -> {
      if (feature.isSuccess()) {
        log.info("Client send message successful: [{}]", rpcMessage);
      } else {
        feature.channel().close();
        resultFuture.completeExceptionally(feature.cause());
        log.error("Client send message failed!", feature.cause());
      }
    });
    return resultFuture;
  }

  public Channel getChannel(InetSocketAddress inetSocketAddress) {
    Channel channel = channelProvider.get(inetSocketAddress);
    if (channel == null) {
      channel = connect(inetSocketAddress);
      channelProvider.set(inetSocketAddress, channel);
    }
    return channel;
  }

  @Override
  public void close() throws Exception {
    eventLoopGroup.shutdownGracefully();
  }

}
