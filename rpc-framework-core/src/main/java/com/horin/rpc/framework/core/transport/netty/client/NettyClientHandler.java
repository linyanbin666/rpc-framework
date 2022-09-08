package com.horin.rpc.framework.core.transport.netty.client;

import com.horin.rpc.framework.common.bean.RpcMessage;
import com.horin.rpc.framework.common.bean.RpcResponse;
import com.horin.rpc.framework.core.constant.RpcConstants;
import com.horin.rpc.framework.core.util.SingletonFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import java.net.InetSocketAddress;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyClientHandler extends SimpleChannelInboundHandler<RpcMessage> {

  private final NettyClient nettyClient;
  private final UnprocessedRequests unprocessedRequests;

  public NettyClientHandler() {
    this.nettyClient = SingletonFactory.getInstance(NettyClient.class);
    this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
  }

  @Override
  @SuppressWarnings("unchecked")
  protected void channelRead0(ChannelHandlerContext ctx, RpcMessage msg) throws Exception {
    log.info("Client receive msg: {}", msg);
    byte messageType = msg.getMessageType();
    if (messageType == RpcConstants.HEARTBEAT_RESPONSE_TYPE) {
      log.info("Heartbeat info: {}", msg.getData());
    } else if (messageType == RpcConstants.RPC_RESPONSE_TYPE){
      RpcResponse<Object> rpcResponse = (RpcResponse<Object>) msg.getData();
      unprocessedRequests.complete(rpcResponse);
    }
  }

  @Override
  public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
    if (!(evt instanceof IdleStateEvent)) {
      super.userEventTriggered(ctx, evt);
      return;
    }
    IdleState state = ((IdleStateEvent) evt).state();
    if (state == IdleState.WRITER_IDLE) {
      log.info("Write idle [{}]", ctx.channel().remoteAddress());
      Channel channel = nettyClient.getChannel((InetSocketAddress) ctx.channel().remoteAddress());
      RpcMessage rpcMessage = RpcMessage.builder()
          .messageType(RpcConstants.HEARTBEAT_REQUEST_TYPE)
          .codec(RpcConstants.DEFAULT_COMPRESS_TYPE)
          .compress(RpcConstants.DEFAULT_COMPRESS_TYPE)
          .data(RpcConstants.PING)
          .build();
      channel.writeAndFlush(rpcMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
    }
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    cause.printStackTrace();
    ctx.close();
  }

}
