package com.horin.rpc.framework.common.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SerializationType {

  HESSION((byte)1, "hession"),
  KYRO((byte)2, "kyro"),
  PROTO_STUFF((byte)3, "protostuff");

  private static final Map<Byte, SerializationType> code2Enum = Arrays.stream(SerializationType.values())
      .collect(Collectors.toMap(SerializationType::getCode, Function.identity()));

  private final byte code;

  private final String desc;

  public static SerializationType ofCode(byte code) {
    return code2Enum.get(code);
  }

}
