package jettyrespackage;

import Dao.ServerConfiguration;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.inject.Provides;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class ServerConfigurationParser {

    /**
     * Build of Configuration object from configuration file
     *
     * @return ServerConfiguration instance
     */
    @Provides
    public static ServerConfiguration parse(String fileName) {
        Gson gson = new Gson();
        try {
            JsonReader jsonReader = new JsonReader(new FileReader(fileName));
            if (jsonReader == null)
                throw new Exception("JsonReader instance failed in the installation process");

            return gson.fromJson(jsonReader, ServerConfiguration.class);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        return null;
    }
}
