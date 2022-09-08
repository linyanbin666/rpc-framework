package com.horin.rpc.framework.common.bean;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RpcServiceConfig {

  /**
   * 服务接口方法版本
   */
  private String version;
  /**
   * 服务接口组版本
   */
  private String group;
  /**
   * 服务实现类
   */
  private Object service;

  public String getRpcServiceName() {
    return this.getServiceName() + this.getGroup() + this.getVersion();
  }

  public String getServiceName() {
    return this.service.getClass().getInterfaces()[0].getCanonicalName();
  }

}