package guice.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import entities.ElasticClientConfigData;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import utils.JsonParser;

public class ElasticserachModule extends AbstractModule {

    public final static String ELASTIC_CONFIG_FILE_NAME = "elasticsearch.config";
    private final ElasticClientConfigData elasticClientConfigData;

    public ElasticserachModule(){
        this.elasticClientConfigData = JsonParser.fromJsonFile(ELASTIC_CONFIG_FILE_NAME, ElasticClientConfigData.class);
    }

    @Provides
    public RestHighLevelClient getElasticClient() {
        return new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost(elasticClientConfigData.getElasticSearchContainerName(), elasticClientConfigData.getPort(), elasticClientConfigData.getScheme())));
    }

}
