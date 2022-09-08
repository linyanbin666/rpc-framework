package com.horin.rpc.framework.example.client;

import com.horin.rpc.framework.example.api.Hello;
import com.horin.rpc.framework.example.api.HelloService;

public class HelloController {

  private final HelloService helloService;

  public HelloController(HelloService helloService) {
    this.helloService = helloService;
  }

  public void test() {
    String result = helloService.hello(new Hello("test", "test"));
    assert "name: test, desc: test".equals(result);
    for (int i = 0; i < 10; i++) {
      System.out.println(helloService.hello(new Hello("name" + i, "desc" + i)));
    }
  }

}
