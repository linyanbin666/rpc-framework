package com.horin.framework.example.server;

import com.horin.framework.example.server.service.HelloServiceImpl;
import com.horin.rpc.framework.common.bean.RpcServiceConfig;
import com.horin.rpc.framework.core.transport.netty.server.NettyServer;

public class ServerBootstrap {

  public static void main(String[] args) {
    NettyServer nettyServer = new NettyServer();
    RpcServiceConfig rpcServiceConfig = RpcServiceConfig.builder()
        .service(new HelloServiceImpl())
        .group("test")
        .version("v1")
        .build();
    nettyServer.registerService(rpcServiceConfig);
    nettyServer.start();
    Runtime.getRuntime()
        .addShutdownHook(new Thread(() -> nettyServer.unRegisterService(rpcServiceConfig)));
  }

}
