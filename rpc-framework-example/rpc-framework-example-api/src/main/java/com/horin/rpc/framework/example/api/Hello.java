package com.horin.rpc.framework.example.api;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Hello implements Serializable {
  private String name;
  private String desc;
}
