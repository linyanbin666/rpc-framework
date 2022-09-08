package com.horin.rpc.framework.common.bean;

import java.io.Serializable;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RpcRequest implements Serializable {

  /**
   * 请求id
   */
  private String requestId;
  /**
   * 请求接口
   */
  private String interfaceName;
  /**
   * 请求接口方法
   */
  private String methodName;
  /**
   * 请求接口参数
   */
  private Object[] args;
  /**
   * 请求接口类型
   */
  private Class<?>[] argTypes;
  /**
   * 请求接口方法版本
   */
  private String version;
  /**
   * 请求接口组版本
   */
  private String group;

  public String getRpcServiceName() {
    return this.getInterfaceName() + this.getGroup() + this.getVersion();
  }

}
