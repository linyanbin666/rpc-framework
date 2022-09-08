package com.horin.rpc.framework.core.transport.netty.server;

import com.horin.rpc.framework.common.bean.RpcMessage;
import com.horin.rpc.framework.common.bean.RpcRequest;
import com.horin.rpc.framework.common.bean.RpcResponse;
import com.horin.rpc.framework.common.enums.ResponseStatusCode;
import com.horin.rpc.framework.core.constant.RpcConstants;
import com.horin.rpc.framework.core.handler.RpcRequestHandler;
import com.horin.rpc.framework.core.util.SingletonFactory;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcMessage> {

  private final RpcRequestHandler rpcRequestHandler = SingletonFactory.getInstance(RpcRequestHandler.class);

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, RpcMessage msg) throws Exception {
    log.info("Server receive msg: {}", msg);
    byte messageType = msg.getMessageType();
    RpcMessage rpcMessage = RpcMessage.builder().compress(RpcConstants.DEFAULT_COMPRESS_TYPE)
        .codec(RpcConstants.DEFAULT_SERIALIZE_TYPE).build();
    if (messageType == RpcConstants.HEARTBEAT_REQUEST_TYPE) {
      rpcMessage.setMessageType(RpcConstants.HEARTBEAT_RESPONSE_TYPE);
      rpcMessage.setData(RpcConstants.PONG);
    } else {
      RpcRequest rpcRequest = (RpcRequest) msg.getData();
      Object result = rpcRequestHandler.handle(rpcRequest);
      rpcMessage.setMessageType(RpcConstants.RPC_RESPONSE_TYPE);
      if (ctx.channel().isActive() && ctx.channel().isWritable()) {
        rpcMessage.setData(RpcResponse.success(result, rpcRequest.getRequestId()));
      } else {
        rpcMessage.setData(RpcResponse.fail(ResponseStatusCode.FAIL));
      }
    }
    ctx.writeAndFlush(rpcMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
  }

  @Override
  public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
    if (!(evt instanceof IdleStateEvent)) {
      super.userEventTriggered(ctx, evt);
      return;
    }
    IdleState state = ((IdleStateEvent) evt).state();
    if (state == IdleState.READER_IDLE) {
      log.info("Read idle, close channel");
      ctx.channel().close();
    }
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    cause.printStackTrace();
    ctx.close();
  }

}
