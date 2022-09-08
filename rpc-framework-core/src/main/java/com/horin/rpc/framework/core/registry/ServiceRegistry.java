package com.horin.rpc.framework.core.registry;

import java.net.InetSocketAddress;
import java.util.List;

public interface ServiceRegistry {

  void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress);

  List<String> findServices(String rpcServiceName);

  void unRegisterService(String rpcServiceName, InetSocketAddress inetSocketAddress);

}