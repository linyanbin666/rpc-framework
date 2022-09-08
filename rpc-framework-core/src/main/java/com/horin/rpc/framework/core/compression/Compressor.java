package com.horin.rpc.framework.core.compression;

public interface Compressor {

  byte[] compress(byte[] data);

  byte[] decompress(byte[] data);

}
