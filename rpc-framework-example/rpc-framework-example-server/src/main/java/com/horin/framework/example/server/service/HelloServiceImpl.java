package com.horin.framework.example.server.service;

import com.horin.rpc.framework.example.api.Hello;
import com.horin.rpc.framework.example.api.HelloService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HelloServiceImpl implements HelloService {

  @Override
  public String hello(Hello hello) {
    log.info("Receive hello msg: {}", hello);
    return String.format("name: %s, desc: %s", hello.getName(), hello.getDesc());
  }

}
