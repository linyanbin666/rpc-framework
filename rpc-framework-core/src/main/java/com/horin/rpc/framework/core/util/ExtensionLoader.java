package com.horin.rpc.framework.core.util;

import com.horin.rpc.framework.core.compression.Compressor;
import com.horin.rpc.framework.core.compression.DefaultCompressor;
import com.horin.rpc.framework.core.loadbalance.DefaultLoadBalance;
import com.horin.rpc.framework.core.loadbalance.LoadBalance;
import com.horin.rpc.framework.core.registry.DefaultServiceDiscovery;
import com.horin.rpc.framework.core.registry.DefaultServiceRegistry;
import com.horin.rpc.framework.core.registry.ServiceDiscovery;
import com.horin.rpc.framework.core.registry.ServiceRegistry;
import com.horin.rpc.framework.core.serialization.DefaultSerializer;
import com.horin.rpc.framework.core.serialization.Serializer;
import com.horin.rpc.framework.core.transport.RpcRequestTransport;
import com.horin.rpc.framework.core.transport.netty.client.NettyClient;
import com.horin.rpc.framework.core.transport.netty.server.NettyServer;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class ExtensionLoader {

  private static final Map<Byte, Serializer> serializerMap;

  private static final Map<Byte, Compressor> compressorMap;

  private static final Map<Byte, ServiceRegistry> serviceRegistryMap;

  private static final Map<Byte, LoadBalance> loadBalanceMap;

  private static final Map<Byte, ServiceDiscovery> serviceDiscoveryMap;

  private static final Map<String, RpcRequestTransport> rpcRequestTransportMap;

  static {
    serializerMap = new HashMap<>();
    serializerMap.put((byte) 1, new DefaultSerializer());
    compressorMap = new HashMap<>();
    compressorMap.put((byte) 1, new DefaultCompressor());
    serviceRegistryMap = new HashMap<>();
    serviceRegistryMap.put((byte) 1, new DefaultServiceRegistry());
    loadBalanceMap = new HashMap<>();
    loadBalanceMap.put((byte) 1, new DefaultLoadBalance());
    serviceDiscoveryMap = new HashMap<>();
    serviceDiscoveryMap.put((byte) 1, new DefaultServiceDiscovery());
    rpcRequestTransportMap = new HashMap<>();
    rpcRequestTransportMap.put("netty", new NettyClient());
  }

  public static Optional<Serializer> getSerializer(byte code) {
    return Optional.ofNullable(serializerMap.get(code));
  }

  public static Optional<Compressor> getCompressor(byte code) {
    return Optional.ofNullable(compressorMap.get(code));
  }

  public static ServiceRegistry getServiceRegistry() {
    return serviceRegistryMap.get((byte) 1);
  }

  public static LoadBalance getLoadBalance() {
    return loadBalanceMap.get((byte) 1);
  }

  public static ServiceDiscovery getServiceDiscovery() {
    return serviceDiscoveryMap.get((byte) 1);
  }

  public static RpcRequestTransport getRpcRequestTransport(String code) {
    return rpcRequestTransportMap.get(code);
  }

}
