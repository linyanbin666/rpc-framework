package com.horin.rpc.framework.core.proxy;

import com.horin.rpc.framework.common.bean.RpcRequest;
import com.horin.rpc.framework.common.bean.RpcResponse;
import com.horin.rpc.framework.common.bean.RpcServiceConfig;
import com.horin.rpc.framework.common.enums.ResponseStatusCode;
import com.horin.rpc.framework.core.exception.RpcException;
import com.horin.rpc.framework.core.transport.RpcRequestTransport;
import com.horin.rpc.framework.core.transport.netty.client.NettyClient;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class RpcClientProxy implements InvocationHandler {

  private static final String INTERFACE_NAME = "interfaceName";

  private final RpcRequestTransport rpcRequestTransport;

  private final RpcServiceConfig rpcServiceConfig;

  public RpcClientProxy(
      RpcRequestTransport rpcRequestTransport,
      RpcServiceConfig rpcServiceConfig) {
    this.rpcRequestTransport = rpcRequestTransport;
    this.rpcServiceConfig = rpcServiceConfig;
  }

  @SuppressWarnings("unchecked")
  public <T> T getProxy(Class<T> clazz) {
    return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
  }

  @Override
  @SuppressWarnings("unchecked")
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    RpcRequest rpcRequest = RpcRequest.builder()
        .interfaceName(method.getDeclaringClass().getName())
        .methodName(method.getName())
        .argTypes(method.getParameterTypes())
        .args(args)
        .requestId(UUID.randomUUID().toString())
        .group(rpcServiceConfig.getGroup())
        .version(rpcServiceConfig.getVersion())
        .build();
    RpcResponse<Object> rpcResponse = null;
    if (rpcRequestTransport instanceof NettyClient) {
      CompletableFuture<RpcResponse<Object>> result = (CompletableFuture<RpcResponse<Object>>) rpcRequestTransport
          .sendRpcRequest(rpcRequest);
      rpcResponse = result.get();
    }
    this.check(rpcResponse, rpcRequest);
    return rpcResponse.getData();
  }

  private void check(RpcResponse<Object> rpcResponse, RpcRequest rpcRequest) {
    if (rpcResponse == null) {
      throw new RpcException(
          String.format("服务调用失败-%s.%s", rpcRequest.getInterfaceName(), rpcRequest.getMethodName()));
    }

    if (!rpcRequest.getRequestId().equals(rpcResponse.getRequestId())) {
      throw new RpcException(String
          .format("返回结果错误！请求和返回的相应不匹配-%s.%s", rpcRequest.getInterfaceName(),
              rpcRequest.getMethodName()));
    }

    if (rpcResponse.getCode() == null || !rpcResponse.getCode()
        .equals(ResponseStatusCode.SUCCESS.getCode())) {
      throw new RpcException(String.format("服务调用失败，返回非正常状态码-%s.%s", rpcRequest.getInterfaceName(),
          rpcRequest.getMethodName()));
    }
  }

}
