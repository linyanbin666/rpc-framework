package com.horin.rpc.framework.core.transport.netty.codec;

import com.horin.rpc.framework.common.bean.RpcMessage;
import com.horin.rpc.framework.common.bean.RpcRequest;
import com.horin.rpc.framework.common.bean.RpcResponse;
import com.horin.rpc.framework.core.constant.RpcConstants;
import com.horin.rpc.framework.core.exception.RpcException;
import com.horin.rpc.framework.core.serialization.Serializer;
import com.horin.rpc.framework.core.util.ExtensionLoader;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import java.nio.charset.StandardCharsets;

public class RpcMessageDecoder extends LengthFieldBasedFrameDecoder {

  public RpcMessageDecoder() {
    super(RpcConstants.MAX_FRAME_LENGTH, 1, 4, -5, 0);
  }

  @Override
  protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
    Object decoded = super.decode(ctx, in);
    if (decoded instanceof ByteBuf) {
      ByteBuf frame = (ByteBuf) decoded;
      return decodeFrame(frame);
    }
    return decoded;
  }

  private Object decodeFrame(ByteBuf frame) {
    byte version = frame.readByte();
    if (version != RpcConstants.PROTOCOL_VERSION) {
      throw new RpcException("协议版本不支持");
    }
    int fullLength = frame.readInt();
    byte messageType = frame.readByte();
    byte codecType = frame.readByte();
    byte compressType = frame.readByte();
    RpcMessage rpcMessage = RpcMessage.builder()
        .messageType(messageType)
        .codec(codecType)
        .compress(compressType).build();
    if (messageType == RpcConstants.HEARTBEAT_REQUEST_TYPE) {
      rpcMessage.setData(RpcConstants.PING);
      return rpcMessage;
    }
    if (messageType == RpcConstants.HEARTBEAT_RESPONSE_TYPE) {
      rpcMessage.setData(RpcConstants.PONG);
      return rpcMessage;
    }

    byte requestIdLength = frame.readByte();
    byte[] requestId = new byte[requestIdLength];
    frame.readBytes(requestId);
    rpcMessage.setRequestId(new String(requestId, StandardCharsets.UTF_8));

    int bodyLength = fullLength - RpcConstants.HEADER_LENGTH - requestIdLength;
    byte[] body = new byte[bodyLength];
    frame.readBytes(body);

    body = ExtensionLoader.getCompressor(compressType)
        .orElseThrow(() -> new UnsupportedOperationException("暂不支持的压缩方式: " + compressType))
        .decompress(body);
    Serializer serializer = ExtensionLoader.getSerializer(codecType)
        .orElseThrow(() -> new UnsupportedOperationException("暂不支持的序列化方式: " + codecType));
    if (messageType == RpcConstants.RPC_REQUEST_TYPE) {
      RpcRequest rpcRequest = serializer.deserialize(body, RpcRequest.class);
      rpcMessage.setData(rpcRequest);
    } else {
      RpcResponse rpcResponse = serializer.deserialize(body, RpcResponse.class);
      rpcMessage.setData(rpcResponse);
    }

    return rpcMessage;
  }

}
