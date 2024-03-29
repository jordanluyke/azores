package com.jordanluyke.azores.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jordanluyke.azores.web.model.FieldRequiredException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.ParseException;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

public class NodeUtil {
    private static final Logger logger = LogManager.getLogger(NodeUtil.class);

    public static ObjectMapper mapper = new ObjectMapper()
            .registerModule(new Jdk8Module())
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
            .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, false)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public static boolean isValidJSON(byte[] json) {
        try {
            return !mapper.readTree(json).isNull();
        } catch(IOException e) {
            return false;
        }
    }

    public static JsonNode getJsonNode(byte[] json) {
        try {
            return mapper.readTree(json);
        } catch(IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static byte[] writeValueAsBytes(Object o) {
        try {
            return mapper.writeValueAsBytes(o);
        } catch(JsonProcessingException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static <T> T parseNodeInto(Class<T> clazz, JsonNode body) throws FieldRequiredException, JsonProcessingException {
        return parseNodeInto(clazz, body, mapper);
    }

    public static <T> T parseNodeInto(Class<T> clazz, JsonNode body, ObjectMapper mapper) throws FieldRequiredException, JsonProcessingException {
        try {
            return mapper.treeToValue(body, clazz);
        } catch(IllegalArgumentException | JsonProcessingException e) {
            logger.error("Json serialize fail: {} {}", clazz, body);
            for(Field field : clazz.getFields()) {
                field.setAccessible(true);
                String name = field.getName();
                if(body.get(name) == null)
                    throw new FieldRequiredException(name);
            }
            if(e instanceof JsonProcessingException)
                throw e;
            throw new RuntimeException(e.getMessage());
        }
    }

    public static JsonNode parseObjectIntoNode(Object object) {
        try {
            return mapper.valueToTree(object);
        } catch(IllegalArgumentException e) {
            logger.error("Object parse failed: {}", object);
            throw new RuntimeException("Object parse failed");
        }
    }

    public static Optional<JsonNode> get(String field, JsonNode node) {
        return Optional.ofNullable(node.get(field));
    }

    public static Optional<String> getString(String field, JsonNode node) {
        JsonNode fieldNode = node.get(field);
        if(fieldNode == null || fieldNode.isNull())
            return Optional.empty();
        return Optional.of(fieldNode.asText());
    }

    public static Optional<Boolean> getBoolean(String field, JsonNode node) {
        return getString(field, node).map(Boolean::valueOf);
    }

    public static Optional<Integer> getInteger(String field, JsonNode node) {
        return getString(field, node).map(Integer::parseInt);
    }

    public static Optional<BigDecimal> getDecimal(String field, JsonNode node) {
        return getString(field, node).map(BigDecimal::new);
    }

    public static <T> Optional<List<T>> getList(String field, JsonNode node, Class<T[]> clazz) {
        return get(field, node)
                .map(n -> List.of(mapper.convertValue(n, clazz)));
    }
}
