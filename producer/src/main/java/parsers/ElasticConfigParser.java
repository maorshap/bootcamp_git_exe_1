package parsers;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import entites.ElasticConfigEntity;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class ElasticConfigParser {
    public static ElasticConfigEntity parse(String fileName){
        Gson gson = new Gson();

        JsonReader jsonReader = null;
        try {
            jsonReader = new JsonReader(new FileReader(fileName));
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        if (jsonReader == null)
            throw new RuntimeException("JsonReader instance failed in the installation process");

        return gson.fromJson(jsonReader, ElasticConfigEntity.class);
    }
}
