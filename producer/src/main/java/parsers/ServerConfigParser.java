package parsers;

import entites.ServerConfigEntity;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class ServerConfigParser {

    /**
     * Build of Configuration object from configuration file
     *
     * @return ServerConfiguration instance
     */
    public static ServerConfigEntity parse(String fileName) {
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

        return gson.fromJson(jsonReader, ServerConfigEntity.class);
    }
}
