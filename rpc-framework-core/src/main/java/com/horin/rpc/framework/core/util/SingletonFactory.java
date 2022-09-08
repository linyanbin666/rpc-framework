package com.horin.rpc.framework.core.util;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class SingletonFactory {

  private static final Map<Class<?>, Object> OBJECT_MAP = new ConcurrentHashMap<>();

  private SingletonFactory() {
  }

  public static <T> T getInstance(Class<T> clazz) {
    if (clazz == null) {
      throw new IllegalArgumentException();
    }
    return clazz.cast(OBJECT_MAP.computeIfAbsent(clazz, c -> {
      try {
        return c.getDeclaredConstructor().newInstance();
      } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
        throw new RuntimeException(e);
      }
    }));
  }

}
