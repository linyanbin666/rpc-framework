package com.horin.rpc.framework.core.registry;

import com.horin.rpc.framework.common.bean.RpcRequest;
import java.net.InetSocketAddress;

public interface ServiceDiscovery {

  InetSocketAddress lookupService(RpcRequest rpcRequest);

}