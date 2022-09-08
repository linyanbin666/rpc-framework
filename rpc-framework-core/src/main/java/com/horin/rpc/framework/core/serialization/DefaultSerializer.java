package com.horin.rpc.framework.core.serialization;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import com.horin.rpc.framework.core.exception.SerializeException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class DefaultSerializer implements Serializer {

  @Override
  public byte[] serialize(Object obj) {
    try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
      HessianOutput hessianOutput = new HessianOutput(bos);
      hessianOutput.writeObject(obj);
      return bos.toByteArray();
    } catch (Exception e) {
      throw new SerializeException("Serialization failed", e);
    }
  }

  @Override
  public <T> T deserialize(byte[] bytes, Class<T> clazz) {
    try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes)) {
      HessianInput hessianInput = new HessianInput(bis);
      Object o = hessianInput.readObject();
      return clazz.cast(o);
    } catch (Exception e) {
      throw new SerializeException("Deserialization failed");
    }
  }

}
