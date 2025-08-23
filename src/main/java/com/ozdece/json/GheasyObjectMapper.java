package com.ozdece.json;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

public class GheasyObjectMapper {

    private GheasyObjectMapper(){}

    private static class ObjectMapperHolder {
        private static final JsonMapper jsonMapper = create();

        private static JsonMapper create(){

            return JsonMapper.builder()
                    .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                    .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .addModule(new Jdk8Module())
                    .addModule(new GuavaModule())
                    .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                    .build();
        }
    }

    public static JsonMapper getJsonMapper() {
        return GheasyObjectMapper.ObjectMapperHolder.jsonMapper;
    }

}
