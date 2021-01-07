package com.github.vas.atanasov.userservice.utils;

import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

@UtilityClass
public final class StreamUtils {

  public static <T> Stream<T> nullSafeStream(Collection<T> collection) {
    return collection == null ? Stream.empty() : collection.stream();
  }

  public static <T> Stream<T> nullSafeStream(Stream<T> stream) {
    return stream == null ? Stream.empty() : stream;
  }

  public static <T> Stream<T> nullSafeStream(T[] array) {
    return array == null ? Stream.empty() : Arrays.stream(array);
  }
}
