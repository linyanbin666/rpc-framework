package com.horin.rpc.framework.core.loadbalance;

import com.horin.rpc.framework.common.bean.RpcRequest;
import java.util.List;
import java.util.Random;

public class DefaultLoadBalance extends AbstractLoadBalance {

  @Override
  protected String doSelect(List<String> serviceAddresses, RpcRequest rpcRequest) {
    Random random = new Random();
    return serviceAddresses.get(random.nextInt(serviceAddresses.size()));
  }

}
