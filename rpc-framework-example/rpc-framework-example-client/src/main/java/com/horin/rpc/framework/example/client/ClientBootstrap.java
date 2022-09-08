package com.horin.rpc.framework.example.client;

import com.horin.rpc.framework.common.bean.RpcServiceConfig;
import com.horin.rpc.framework.core.proxy.RpcClientProxy;
import com.horin.rpc.framework.core.transport.RpcRequestTransport;
import com.horin.rpc.framework.core.util.ExtensionLoader;
import com.horin.rpc.framework.example.api.HelloService;

public class ClientBootstrap {

  public static void main(String[] args) {
    RpcRequestTransport rpcRequestTransport = ExtensionLoader.getRpcRequestTransport("netty");
    RpcServiceConfig rpcServiceConfig = RpcServiceConfig.builder()
        .group("test")
        .version("v1")
        .build();
    HelloService helloService = new RpcClientProxy(rpcRequestTransport, rpcServiceConfig).getProxy(HelloService.class);
    HelloController helloController = new HelloController(helloService);
    helloController.test();
  }

}
