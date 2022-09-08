package com.horin.rpc.framework.core.serialization;

public interface Serializer {

  byte[] serialize(Object obj);

  <T> T deserialize(byte[] data, Class<T> clazz);

}
