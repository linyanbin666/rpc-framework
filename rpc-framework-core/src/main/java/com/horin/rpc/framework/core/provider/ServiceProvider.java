package com.horin.rpc.framework.core.provider;

import com.horin.rpc.framework.common.bean.RpcServiceConfig;

public interface ServiceProvider {

  void addService(RpcServiceConfig rpcServiceConfig);

  Object getService(String rpcServiceName);

  void publishService(RpcServiceConfig rpcServiceConfig);

  void removeService(RpcServiceConfig rpcServiceConfig);

}