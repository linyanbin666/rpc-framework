package com.horin.rpc.framework.core.provider;

import com.horin.rpc.framework.common.bean.RpcServiceConfig;
import com.horin.rpc.framework.core.exception.RpcException;
import com.horin.rpc.framework.core.registry.ServiceRegistry;
import com.horin.rpc.framework.core.transport.netty.server.NettyServer;
import com.horin.rpc.framework.core.util.ExtensionLoader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultServiceProvider implements ServiceProvider {

  private final Map<String, Object> serviceMap = new ConcurrentHashMap<>();
  private final ServiceRegistry serviceRegistry;

  public DefaultServiceProvider() {
    serviceRegistry = ExtensionLoader.getServiceRegistry();
  }

  @Override
  public void addService(RpcServiceConfig rpcServiceConfig) {
    if (serviceMap.containsKey(rpcServiceConfig.getRpcServiceName())) {
      return;
    }
    serviceMap.put(rpcServiceConfig.getRpcServiceName(), rpcServiceConfig.getService());
  }

  @Override
  public Object getService(String rpcServiceName) {
    return serviceMap.get(rpcServiceName);
  }

  @Override
  public void publishService(RpcServiceConfig rpcServiceConfig) {
    try {
      serviceRegistry.registerService(rpcServiceConfig.getRpcServiceName(),
          new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), NettyServer.PORT));
      addService(rpcServiceConfig);
      log.info("Publish service success, rpcServiceConfig: {}", rpcServiceConfig);
    } catch (UnknownHostException e) {
      throw new RpcException("注册服务失败!");
    }
  }

  @Override
  public void removeService(RpcServiceConfig rpcServiceConfig) {
    try {
      serviceMap.remove(rpcServiceConfig.getRpcServiceName());
      serviceRegistry.unRegisterService(rpcServiceConfig.getRpcServiceName(), new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), NettyServer.PORT));
    } catch (UnknownHostException e) {
      throw new RpcException("解除服务注册失败!");
    }
  }

}
