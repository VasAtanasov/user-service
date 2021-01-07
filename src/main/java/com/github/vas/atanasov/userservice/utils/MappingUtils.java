package com.github.vas.atanasov.userservice.utils;

import lombok.experimental.UtilityClass;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.TypeToken;
import org.modelmapper.config.Configuration;
import org.modelmapper.convention.MatchingStrategies;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Helper class for {@link ModelMapper}.Two instances are created lazy. One with configuration and
 * one without. The utility class provides methods for mapping entities to target class/instance
 * with various overloaded methods.
 */
@UtilityClass
public final class MappingUtils {

  private static ModelMapper configured;
  private static ModelMapper defaultMapper;

  public static ModelMapper createDefault() {
    return new ModelMapper();
  }

  public static ModelMapper createConfigured() {
    ModelMapper modelMapper = new ModelMapper();
    modelMapper
        .getConfiguration()
        .setAmbiguityIgnored(true)
        .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE)
        .setMatchingStrategy(MatchingStrategies.STRICT)
        .setFieldMatchingEnabled(true);
    return modelMapper;
  }

  private static ModelMapper getConfigured() {
    if (configured == null) {
      configured = createConfigured();
    }
    return configured;
  }

  private static ModelMapper getDefault() {
    if (defaultMapper == null) {
      defaultMapper = createDefault();
    }
    return defaultMapper;
  }

  public static <S, D> D map(final S entity, Class<D> outClass) {
    return map(entity, outClass, null, null);
  }

  public static <S, D> D map(S entity, Class<D> outClass, PropertyMap<S, D> propertyMap) {
    return map(entity, outClass, null, propertyMap);
  }

  public static <S, D> D map(S entity, Class<D> outClass, ModelMapper modelMapper) {
    return map(entity, outClass, modelMapper, null);
  }

  /**
   * Instantiates and maps properties from source entity to the target instantiated with the help of
   * custom {@link PropertyMap} and custom {@link ModelMapper}. if {@link ModelMapper} is null a
   * configured one will be provided.
   *
   * @param entity source entity
   * @param outClass target entity class
   * @param modelMapper custom configured {@link ModelMapper}
   * @param propertyMap custom {@link PropertyMap}
   * @return mapped instance of the target class
   */
  public static <S, D> D map(
      S entity, Class<D> outClass, ModelMapper modelMapper, PropertyMap<S, D> propertyMap) {
    ModelMapper mapper = modelMapper;
    Objects.requireNonNull(entity, "Source object must not be null");
    Objects.requireNonNull(outClass, "Destination type must not be null");
    if (modelMapper == null) mapper = getConfigured();
    if (propertyMap != null) mapper.addMappings(propertyMap);
    return mapper.map(entity, outClass);
  }

  public static <S, D> List<D> mapAll(final Collection<S> entityList, Class<D> outCLass) {
    Stream<S> safeStream = StreamUtils.nullSafeStream(entityList);
    return mapAll(safeStream, outCLass, null, null);
  }

  public static <S, D> List<D> mapAll(
      final Collection<S> entityList, Class<D> outCLass, PropertyMap<S, D> propertyMap) {
    Stream<S> safeStream = StreamUtils.nullSafeStream(entityList);
    return mapAll(safeStream, outCLass, null, propertyMap);
  }

  public static <S, D> List<D> mapAll(
      final Collection<S> entityList, Class<D> outCLass, ModelMapper modelMapper) {
    Stream<S> safeStream = StreamUtils.nullSafeStream(entityList);
    return mapAll(safeStream, outCLass, modelMapper, null);
  }

  /**
   * Maps stream of entities with the help of custom {@link PropertyMap} and custom {@link ModelMapper}.
   * if {@link ModelMapper} is null a configured one will be provided.
   *
   * @param entityStream source stream
   * @param outClass target entity class
   * @param modelMapper custom configured {@link ModelMapper}
   * @param propertyMap custom {@link PropertyMap}
   * @return mapped instance of the target class
   */
  public static <S, D> List<D> mapAll(
      final Stream<S> entityStream,
      Class<D> outClass,
      ModelMapper modelMapper,
      PropertyMap<S, D> propertyMap) {
    Stream<S> safeStream = StreamUtils.nullSafeStream(entityStream);
    return safeStream
        .map(entity -> map(entity, outClass, modelMapper, propertyMap))
        .collect(Collectors.toList());
  }

  public static <S, D> List<D> map(List<S> source, TypeToken<List<D>> typeToken) {
    return map(source, typeToken, null, null);
  }

  public static <S, D> List<D> map(
      List<S> source, TypeToken<List<D>> typeToken, ModelMapper modelMapper) {
    return map(source, typeToken, modelMapper, null);
  }

  public static <S, D> List<D> map(
      List<S> source, TypeToken<List<D>> typeToken, PropertyMap<S, D> propertyMap) {
    return map(source, typeToken, null, propertyMap);
  }

  /**
   * Maps stream of entities with the help of custom {@link PropertyMap} and custom {@link ModelMapper}.
   * if {@link ModelMapper} is null a configured one will be provided.
   *
   * @param source {@link List} of entities
   * @param typeToken {@link TypeToken} provided for mapping collections
   * @param modelMapper custom configured {@link ModelMapper}
   * @param propertyMap custom {@link PropertyMap}
   * @return mapped instance of the target class
   */
  public static <S, D> List<D> map(
      List<S> source,
      TypeToken<List<D>> typeToken,
      ModelMapper modelMapper,
      PropertyMap<S, D> propertyMap) {
    if (source == null && typeToken == null) {
      return Collections.emptyList();
    }
    ModelMapper mapper = modelMapper;
    if (modelMapper == null) mapper = getConfigured();
    if (propertyMap != null) mapper.addMappings(propertyMap);
    return mapper.map(source, typeToken.getType());
  }

  public static <S, D> D map(final S source, D destination) {
    return mapObjects(source, destination, null, null);
  }

  public static <S, D> D map(final S source, D destination, PropertyMap<S, D> propertyMap) {
    return mapObjects(source, destination, null, propertyMap);
  }

  public static <S, D> D map(final S source, D destination, ModelMapper modelMapper) {
    return mapObjects(source, destination, modelMapper, null);
  }

  /**
   * Maps source entity to target entity with the help of custom {@link PropertyMap} and custom
   * {@link ModelMapper}. if {@link ModelMapper} is null a configured one will be provided. Use this
   * method to copy fields from source entity to destination.
   *
   * @param entity source entity
   * @param destination destination entity
   * @param modelMapper custom configured {@link ModelMapper}
   * @param propertyMap custom {@link PropertyMap}
   * @return mapped instance of the target class
   */
  public static <S, D> D mapObjects(
      S entity, D destination, ModelMapper modelMapper, PropertyMap<S, D> propertyMap) {
    ModelMapper mapper = modelMapper;
    Objects.requireNonNull(entity, "Source object must not be null");
    Objects.requireNonNull(destination, "Destination type must not be null");
    if (modelMapper == null) mapper = getConfigured();
    if (propertyMap != null) mapper.addMappings(propertyMap);
    mapper.map(entity, destination);
    return destination;
  }
}
