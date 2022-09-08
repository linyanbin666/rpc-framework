package com.horin.rpc.framework.core.constant;

import com.horin.rpc.framework.common.enums.CompressType;
import com.horin.rpc.framework.common.enums.SerializationType;

public final class RpcConstants {

  public static final byte PROTOCOL_VERSION = 1;

  public static final byte HEADER_LENGTH = 9;

  public static final byte HEARTBEAT_REQUEST_TYPE = 1;

  public static final byte HEARTBEAT_RESPONSE_TYPE = 2;

  public static final byte RPC_REQUEST_TYPE = 3;

  public static final byte RPC_RESPONSE_TYPE = 4;

  public static final int MAX_FRAME_LENGTH = 8 * 1024 * 1024;

  public static final String PING = "ping";

  public static final String PONG = "pong";

  public static final byte DEFAULT_COMPRESS_TYPE = CompressType.NONE.getCode();

  public static final byte DEFAULT_SERIALIZE_TYPE = SerializationType.HESSION.getCode();

}
