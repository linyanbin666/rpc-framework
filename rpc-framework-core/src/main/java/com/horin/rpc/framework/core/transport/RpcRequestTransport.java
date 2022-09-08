package com.horin.rpc.framework.core.transport;

import com.horin.rpc.framework.common.bean.RpcRequest;

public interface RpcRequestTransport {

  Object sendRpcRequest(RpcRequest rpcRequest);

}
