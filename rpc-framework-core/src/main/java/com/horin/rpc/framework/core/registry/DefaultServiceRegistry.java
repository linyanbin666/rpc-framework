package com.horin.rpc.framework.core.registry;

import com.horin.rpc.framework.core.exception.RpcException;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultServiceRegistry implements ServiceRegistry {

  private static final String REGISTER_PATH_PREFIX = "register/";

  @Override
  public synchronized void registerService(String rpcServiceName,
      InetSocketAddress inetSocketAddress) {
    String serviceUrl = inetSocketAddress.toString().substring(1);
    registerPathIfNotExists(rpcServiceName, serviceUrl);
  }

  @Override
  public synchronized List<String> findServices(String rpcServiceName) {
    return readPaths(rpcServiceName);
  }

  @Override
  public void unRegisterService(String rpcServiceName, InetSocketAddress inetSocketAddress) {
    try {
      String serviceUrl = inetSocketAddress.toString().substring(1).replace(":", "_");
      Files.list(Paths.get(REGISTER_PATH_PREFIX + rpcServiceName))
          .filter(path -> path.toFile().getName().equals(serviceUrl))
          .forEach(path -> {
            try {
              Files.deleteIfExists(path);
            } catch (IOException e) {
              // ignored
            }
          });
    } catch (IOException e) {
      throw new RpcException("清除注册信息失败!", e);
    }
  }

  private List<String> readPaths(String rpcServiceName) {
    try {
      File registerFile = new File(REGISTER_PATH_PREFIX + rpcServiceName);
      if (!registerFile.exists()) {
        return new ArrayList<>();
      }
      return Files.list(registerFile.toPath())
          .map(path -> path.toFile().getName().replace("_", ":"))
          .collect(Collectors.toList());
    } catch (IOException e) {
      throw new RpcException("服务注册失败!", e);
    }
  }

  private void registerPathIfNotExists(String rpcServiceName, String serviceUrl) {
    try {
      File registerFile = new File(
          REGISTER_PATH_PREFIX + rpcServiceName + File.separator + serviceUrl.replace(":", "_"));
      if (!registerFile.exists()) {
        File parentFile = registerFile.getParentFile();
        if (!parentFile.exists()) {
          parentFile.mkdirs();
        }
        Files.createFile(registerFile.toPath());
      }
    } catch (IOException e) {
      throw new RpcException("服务注册失败!", e);
    }
  }

}
