package com.horin.rpc.framework.common.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CompressType {

  NONE((byte)1, "none")
  ;

  private static final Map<Byte, CompressType> code2Enum = Arrays.stream(CompressType.values())
      .collect(Collectors.toMap(CompressType::getCode, Function.identity()));

  private final byte code;

  private final String desc;

  public static CompressType ofCode(byte code) {
    return code2Enum.get(code);
  }

}
