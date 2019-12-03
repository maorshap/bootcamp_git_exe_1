package jettyrespackage;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import io.logz.guice.jersey.JerseyModule;
import io.logz.guice.jersey.configuration.JerseyConfiguration;

import org.apache.http.HttpHost;

import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.FileNotFoundException;
import java.io.FileReader;




public class ServerModule extends AbstractModule {
    JerseyConfiguration jerseyConfiguration;

    public ServerModule(String package_path){
        jerseyConfiguration = JerseyConfiguration.builder()
                .addPackage(package_path)
                .addPort(getServerConfiguration().getPort())
                .build();
    }

    @Override
    protected void configure(){
        install(new JerseyModule(jerseyConfiguration));
    }

    /**
     * Creates Configuration object from configuration file
     * @return ServerConfiguration instance
     */
    @Provides
    public ServerConfiguration getServerConfiguration(){
        Gson gson = new Gson();
        JsonReader jsonReader = null;
        try{
            jsonReader = new JsonReader(new FileReader("server.config"));
            if(jsonReader == null)
                throw new Exception("JsonReader instance failed in the installation process");
            return gson.fromJson(jsonReader, ServerConfiguration.class);
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }catch(Exception e){
            System.out.println(e.getMessage());
        }

        return null;
    }

    @Provides
    public RestHighLevelClient getElasticClient(){
      return new RestHighLevelClient(
        RestClient.builder(
                new HttpHost("elastic_search_container", 9200, "http"),
                new HttpHost("elastic_search_container", 9201, "http")));
    }

}

