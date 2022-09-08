package com.horin.rpc.framework.core.transport.netty.codec;

import com.horin.rpc.framework.common.bean.RpcMessage;
import com.horin.rpc.framework.core.constant.RpcConstants;
import com.horin.rpc.framework.core.util.ExtensionLoader;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * version(1 byte) + length(4 byte) +  messageType(1 byte) + codec(1 byte) + compress(1 byte) +
 * requestId[len(1 byte) + value] + data
 */
public class RpcMessageEncoder extends MessageToByteEncoder<RpcMessage> {

  @Override
  protected void encode(ChannelHandlerContext ctx, RpcMessage msg, ByteBuf out) throws Exception {
    out.writeByte(RpcConstants.PROTOCOL_VERSION);
    out.writerIndex(out.writerIndex() + 4);
    byte messageType = msg.getMessageType();
    out.writeByte(messageType);
    out.writeByte(msg.getCodec());
    out.writeByte(msg.getCompress());
    byte[] requestId = UUID.randomUUID().toString().replace("-", "").getBytes(
        StandardCharsets.UTF_8);
    out.writeByte(requestId.length);
    out.writeBytes(requestId);
    byte[] data = null;
    int fullLength = RpcConstants.HEADER_LENGTH + requestId.length;
    if (messageType != RpcConstants.HEARTBEAT_REQUEST_TYPE
        && messageType != RpcConstants.HEARTBEAT_RESPONSE_TYPE) {
      data = ExtensionLoader.getSerializer(msg.getCodec())
          .orElseThrow(() -> new UnsupportedOperationException("暂不支持的序列化方式: " + msg.getCodec()))
          .serialize(msg.getData());
      data = ExtensionLoader.getCompressor(msg.getCompress())
          .orElseThrow(() -> new UnsupportedOperationException("暂不支持的压缩方式: " + msg.getCodec()))
          .compress(data);
    }
    if (data != null) {
      fullLength += data.length;
      out.writeBytes(data);
    }
    int writerIndex = out.writerIndex();
    out.writerIndex(writerIndex - fullLength + 1);
    out.writeInt(fullLength);
    out.writerIndex(writerIndex);
  }

}
