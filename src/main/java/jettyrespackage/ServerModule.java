package jettyrespackage;

import Dao.ServerConfiguration;
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
    private final JerseyConfiguration jerseyConfiguration;
    private final ServerConfiguration serverConfiguration;

    public ServerModule(String packagePath, ServerConfiguration serverConfiguration) {
        this.serverConfiguration = serverConfiguration;

        jerseyConfiguration = JerseyConfiguration.builder()
                .addPackage(packagePath)
                .addPort(serverConfiguration.getPort())
                .build();
    }

    @Override
    protected void configure() {
        install(new JerseyModule(jerseyConfiguration));
    }


    @Provides
    public RestHighLevelClient getElasticClient() {
        return new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost(serverConfiguration.getElasticSearchContainerName(), 9200, "http"),
                        new HttpHost(serverConfiguration.getElasticSearchContainerName(), 9201, "http")));
    }

    @Provides
    public ServerConfiguration getServerConfiguration(){
        return this.serverConfiguration;
    }

}

