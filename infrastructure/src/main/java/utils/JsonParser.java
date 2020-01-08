package utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class JsonParser {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * Transform Object into json String.
     * @param obj
     * @param <T>
     * @return String in Json scheme
     */
    public static <T> String toJsonString(T obj){
        try{
            return OBJECT_MAPPER.writeValueAsString(obj);
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    /**
     * Transform String in json scheme into object
     * @param jsonString
     * @param clazz
     * @param <T>
     * @return class instance from json String
     */
    public static <T> T fromJsonString(String jsonString, Class<T> clazz){
        try{
            return OBJECT_MAPPER.readValue(jsonString, clazz);
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    /**
     * Initialize Class T instance from file.
     * @param fileName
     * @param clazz
     * @param <T>
     * @return class instance from json file.
     */
    public static <T> T fromJsonFile(String fileName, Class<T> clazz){
        JsonReader jsonReader = null;

        try {
            jsonReader = new JsonReader(new FileReader(fileName));
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        if (jsonReader == null)
            throw new RuntimeException("JsonReader instance failed in the installation process");

        Gson gson = new Gson();
        return gson.fromJson(jsonReader, clazz);
    }

}
