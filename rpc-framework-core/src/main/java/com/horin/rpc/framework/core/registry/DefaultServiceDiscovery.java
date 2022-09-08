package com.horin.rpc.framework.core.registry;

import com.horin.rpc.framework.common.bean.RpcRequest;
import com.horin.rpc.framework.core.exception.RpcException;
import com.horin.rpc.framework.core.loadbalance.LoadBalance;
import com.horin.rpc.framework.core.util.ExtensionLoader;
import java.net.InetSocketAddress;
import java.util.List;

public class DefaultServiceDiscovery implements ServiceDiscovery {

  private final LoadBalance loadBalance;
  private final ServiceRegistry serviceRegistry;

  public DefaultServiceDiscovery() {
    this.loadBalance = ExtensionLoader.getLoadBalance();
    this.serviceRegistry = ExtensionLoader.getServiceRegistry();
  }

  @Override
  public InetSocketAddress lookupService(RpcRequest rpcRequest) {
    String rpcServiceName = rpcRequest.getRpcServiceName();
    List<String> serviceUrlList = serviceRegistry.findServices(rpcServiceName);
    if (serviceUrlList.isEmpty()) {
      throw new RpcException("找不到可用服务!");
    }
    String serviceAddress = loadBalance.selectServiceAddress(serviceUrlList, rpcRequest);
    String[] hostAndPort = serviceAddress.split(":");
    return new InetSocketAddress(hostAndPort[0], Integer.parseInt(hostAndPort[1]));
  }

}
