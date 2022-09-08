package com.horin.rpc.framework.core.compression;

public class DefaultCompressor implements Compressor {

  @Override
  public byte[] compress(byte[] data) {
    return data;
  }

  @Override
  public byte[] decompress(byte[] data) {
    return data;
  }

}
