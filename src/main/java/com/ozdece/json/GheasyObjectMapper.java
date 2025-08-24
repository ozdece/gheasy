package com.ozdece.json;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class GheasyObjectMapper {

    private GheasyObjectMapper(){}

    private static class ObjectMapperHolder {
        private static final JsonMapper snakeCaseJsonMapper = create(PropertyNamingStrategies.SNAKE_CASE);
        private static final JsonMapper defaultJsonMapper = create(PropertyNamingStrategies.LOWER_CAMEL_CASE);

        private static JsonMapper create(PropertyNamingStrategy propertyNamingStrategy){

            return JsonMapper.builder()
                    .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                    .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                    .addModule(new Jdk8Module())
                    .addModule(new GuavaModule())
                    .addModule(new JavaTimeModule())
                    .propertyNamingStrategy(propertyNamingStrategy)
                    .build();
        }
    }

    public static JsonMapper getSnakeCaseJsonMapper() {
        return ObjectMapperHolder.snakeCaseJsonMapper;
    }

    public static JsonMapper getDefaultJsonMapper() {
        return ObjectMapperHolder.defaultJsonMapper;
    }

}
