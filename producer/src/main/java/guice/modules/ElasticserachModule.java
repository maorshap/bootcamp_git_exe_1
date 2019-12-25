package guice.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import configuration.util.ConfigurationLoader;
import entites.ElasticConfigEntity;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

public class ElasticserachModule extends AbstractModule {

    public final static String ELASTIC_CONFIG_FILE_NAME = "elasticsearch.config";
    private final ElasticConfigEntity elasticConfigEntity;

    public ElasticserachModule(){
        this.elasticConfigEntity = ConfigurationLoader.load(ELASTIC_CONFIG_FILE_NAME, ElasticConfigEntity.class);
    }

    @Provides
    public RestHighLevelClient getElasticClient() {
        return new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost(elasticConfigEntity.getElasticSearchContainerName(), elasticConfigEntity.getPort(), elasticConfigEntity.getScheme())));
    }

}
