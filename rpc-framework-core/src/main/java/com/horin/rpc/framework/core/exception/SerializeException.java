package com.horin.rpc.framework.core.exception;

public class SerializeException extends RuntimeException {

  public SerializeException(String message) {
    super(message);
  }

  public SerializeException(String message, Throwable cause) {
    super(message, cause);
  }

}
