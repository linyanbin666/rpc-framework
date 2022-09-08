package com.horin.rpc.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseStatusCode {

  SUCCESS(200, "请求成功"),

  FAIL(500, "请求失败");

  private final int code;

  private final String message;

}