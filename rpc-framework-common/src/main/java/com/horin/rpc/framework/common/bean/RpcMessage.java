package com.horin.rpc.framework.common.bean;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RpcMessage {
    /**
     * 消息类型
     */
    private byte messageType;
    /**
     * 序列化类型
     */
    private byte codec;
    /**
     * 压缩类型
     */
    private byte compress;
    /**
     * 请求id
     */
    private String requestId;
    /**
     * 请求数据
     */
    private Object data;
}
