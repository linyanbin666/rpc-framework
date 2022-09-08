package com.horin.rpc.framework.core.loadbalance;

import com.horin.rpc.framework.common.bean.RpcRequest;
import java.util.List;

public interface LoadBalance {

  String selectServiceAddress(List<String> serviceUrlList, RpcRequest rpcRequest);

}