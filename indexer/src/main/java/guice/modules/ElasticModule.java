package guice.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import configuration.util.ConfigurationLoader;
import entities.ElasticProducerConfigEntity;
import entities.ElasticsearchConfigEntity;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

public class ElasticModule extends AbstractModule {
    public final static String ELASTICSEARCH_CONFIG_FILE_NAME = "elasticsearch.config";
    public final static String ELASTIC_PRODUCER_CONFIG_FILE_NAME = "elastic_producer.config";

    private final ElasticsearchConfigEntity elasticsearchConfigEntity;
    private final ElasticProducerConfigEntity elasticProducerConfigEntity;

    public ElasticModule() {
        this.elasticsearchConfigEntity = ConfigurationLoader.load(ELASTICSEARCH_CONFIG_FILE_NAME, ElasticsearchConfigEntity.class);
        this.elasticProducerConfigEntity = ConfigurationLoader.load(ELASTIC_PRODUCER_CONFIG_FILE_NAME, ElasticProducerConfigEntity.class);
    }

    @Provides
    public RestHighLevelClient getElasticClient() {
        return new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost(elasticsearchConfigEntity.getElasticSearchContainerName(), elasticsearchConfigEntity.getPort(), elasticsearchConfigEntity.getScheme())));
    }

    @Provides
    public ElasticProducerConfigEntity getElasticProducerConfigEntity() {
        return elasticProducerConfigEntity;
    }
}
