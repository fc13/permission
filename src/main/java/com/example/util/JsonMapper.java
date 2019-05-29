package com.example.util;


import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.ser.impl.SimpleFilterProvider;
import org.codehaus.jackson.type.TypeReference;

@Slf4j
public class JsonMapper {
    private static ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
        mapper.setFilters(new SimpleFilterProvider().setFailOnUnknownId(false));
        mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_EMPTY);
    }

    public static <T> String objectToString(T t) {
        if (t == null) {
            return null;
        }
        try {
            return t instanceof String ? (String) t : mapper.writeValueAsString(t);
        } catch (Exception e) {
            log.error("parse Object to String occur error : {}", e);
            return null;
        }
    }

    public static <T> T stringToObject(String src, TypeReference<T> typeReference) {
        if (src == null || typeReference == null) {
            return null;
        }
        try {
            return (T) (typeReference.getType().equals(String.class) ? src : mapper.readValue(src, typeReference));
        } catch (Exception e) {
            log.error("parse String to Objct occur error,String:{},Object:{},error:{}",src,typeReference.getType(),e);
            return null;
        }
    }
}
