package com.horin.rpc.framework.common.bean;

import com.horin.rpc.framework.common.enums.ResponseStatusCode;
import java.io.Serializable;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RpcResponse<T> implements Serializable {

  /**
   * 请求id
   */
  private String requestId;
  /**
   * 响应码
   */
  private Integer code;
  /**
   * 响应消息
   */
  private String message;
  /**
   * 响应数据
   */
  private T data;

  public static <T> RpcResponse<T> success(T data, String requestId) {
    return new RpcResponseBuilder<T>()
        .requestId(requestId)
        .code(ResponseStatusCode.SUCCESS.getCode())
        .message(ResponseStatusCode.SUCCESS.getMessage())
        .data(data)
        .build();
  }

  public static <T> RpcResponse<T> fail(ResponseStatusCode statusCode) {
    return new RpcResponseBuilder<T>()
        .code(statusCode.getCode())
        .message(statusCode.getMessage())
        .build();
  }

}
