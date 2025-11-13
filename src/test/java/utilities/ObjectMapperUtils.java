package utilities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.javafaker.Faker;

import java.io.File;
import java.io.IOException;

public class ObjectMapperUtils {

    //Reusable method to convert string json to java object
    public static <T> T convertJsonToJava(String json, Class<T> cls) {//Generic Method
        try {
            return new ObjectMapper().readValue(json, cls);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    //This method gets the file name under test_data and returns that json file as JsonNode object
    public static JsonNode getJsonNode(String fileName) {

        try {
            return new ObjectMapper().readTree(new File("src/test/resources/test_data/" + fileName + ".json"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static void updateJsonNode(JsonNode payload, String fieldName, String value) {
        ObjectNode objectNode = (ObjectNode) payload;
        objectNode.put(fieldName, value);
    }

    public static void updateJsonNode(JsonNode payload, String fieldName, int value) {
        ObjectNode objectNode = (ObjectNode) payload;
        objectNode.put(fieldName, value);
    }

    public static void updateJsonNode(JsonNode payload, String fieldName, double value) {
        ObjectNode objectNode = (ObjectNode) payload;
        objectNode.put(fieldName, value);
    }

    public static void updateJsonNode(JsonNode payload, String fieldName, boolean value) {
        ObjectNode objectNode = (ObjectNode) payload;
        objectNode.put(fieldName, value);
    }

    public static void removeFieldJsonNode(JsonNode payload, String fieldName) {
        ObjectNode objectNode = (ObjectNode) payload;
        objectNode.remove(fieldName);
    }


}
