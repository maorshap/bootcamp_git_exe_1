package configuration.util;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class ConfigurationLoader {

    private ConfigurationLoader(){}

    public static <T> T load(String fileName, Class<T> tClass){
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
        return gson.fromJson(jsonReader, tClass);
    }
}
