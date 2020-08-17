package com.jordanluyke.azores.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Optional;

/**
 * @author Jordan Luyke <jordanluyke@gmail.com>
 */
public class NodeUtil {
    private static final Logger logger = LogManager.getLogger(NodeUtil.class);

    public static ObjectMapper mapper = new ObjectMapper()
            .registerModule(new Jdk8Module())
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
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

    public static JsonNode getJsonNode(String json) {
        try {
            return mapper.readTree(json);
        } catch(IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static byte[] writeValueAsBytes(JsonNode node) {
        try {
            return mapper.writeValueAsBytes(node);
        } catch(JsonProcessingException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static String writeValueAsString(JsonNode node) {
        try {
            return mapper.writeValueAsString(node);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static <T> T parseNodeInto(Class<T> clazz, JsonNode body) {
        try {
            return mapper.treeToValue(body, clazz);
        } catch(JsonProcessingException e) {
            logger.error("Json serialize fail: {} {}", clazz.getSimpleName(), writeValueAsString(body));
            for(Field field : clazz.getFields()) {
                field.setAccessible(true);
                String name = field.getName();
                if(body.get(name) == null)
                    throw new RuntimeException("Field not found: " + name);
            }
            throw new RuntimeException(e.getMessage());
        }
    }

    public static Optional<JsonNode> get(String field, JsonNode node) {
        JsonNode childNode = node.get(field);
        if(childNode == null || childNode.isNull())
            return Optional.empty();
        return Optional.of(childNode);
    }

    public static JsonNode getOrThrow(String field, JsonNode node) {
        Optional<JsonNode> childNode = get(field, node);
        if(childNode.isEmpty())
            throw new RuntimeException("Field not present: " + field);
        return childNode.get();
    }

    public static Optional<String> getString(String field, JsonNode node) {
        JsonNode childNode = node.get(field);
        if(childNode == null || childNode.isNull())
            return Optional.empty();
        return Optional.of(childNode.asText());
    }

    public static String getStringOrThrow(String field, JsonNode node) {
        Optional<String> value = getString(field, node);
        if(value.isEmpty())
            throw new RuntimeException("Field not present: " + field);
        return value.get();
    }

    public static Optional<Boolean> getBoolean(String field, JsonNode node) {
        JsonNode childNode = node.get(field);
        if(childNode == null || childNode.isNull())
            return Optional.empty();
        return Optional.of(childNode.booleanValue());
    }

    public static Boolean getBooleanOrThrow(String field, JsonNode node) {
        Optional<Boolean> value = getBoolean(field, node);
        if(value.isEmpty())
            throw new RuntimeException("Field not present: " + field);
        return value.get();
    }

    public static Optional<Integer> getInteger(String field, JsonNode node) {
        return getString(field, node).map(Integer::parseInt);
    }

    public static Integer getIntegerOrThrow(String field, JsonNode node) {
        Optional<Integer> value = getInteger(field, node);
        if(value.isEmpty())
            throw new RuntimeException("Field not present: " + field);
        return value.get();
    }
}
