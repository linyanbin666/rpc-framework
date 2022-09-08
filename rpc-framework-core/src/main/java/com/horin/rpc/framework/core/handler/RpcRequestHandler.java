package com.horin.rpc.framework.core.handler;

import com.horin.rpc.framework.common.bean.RpcRequest;
import com.horin.rpc.framework.core.exception.RpcException;
import com.horin.rpc.framework.core.provider.DefaultServiceProvider;
import com.horin.rpc.framework.core.provider.ServiceProvider;
import com.horin.rpc.framework.core.util.SingletonFactory;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RpcRequestHandler {

  private final ServiceProvider serviceProvider;

  public RpcRequestHandler() {
    this.serviceProvider = SingletonFactory.getInstance(DefaultServiceProvider.class);
  }

  public Object handle(RpcRequest rpcRequest) {
    Object service = serviceProvider.getService(rpcRequest.getRpcServiceName());
    Object result;
    try {
      Method method = service.getClass()
          .getMethod(rpcRequest.getMethodName(), rpcRequest.getArgTypes());
      result = method.invoke(service, rpcRequest.getArgs());
    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      throw new RpcException(e.getMessage(), e);
    }
    return result;
  }


}
