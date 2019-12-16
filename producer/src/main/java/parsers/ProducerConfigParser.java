package parsers;


import entites.ProducerConfigEntity;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class ProducerConfigParser {

    public static ProducerConfigEntity parse(String fileName){
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
        return gson.fromJson(jsonReader, ProducerConfigEntity.class);
    }
}
