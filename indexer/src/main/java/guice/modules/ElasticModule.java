package guice.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import entities.EsIndexerConfigData;
import entities.EsClientConfigData;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import utils.JsonParser;

public class ElasticModule extends AbstractModule {
    public final static String ELASTICSEARCH_CONFIG_FILE_NAME = "elasticsearch.config";
    public final static String ELASTIC_PRODUCER_CONFIG_FILE_NAME = "elastic_producer.config";

    private final EsClientConfigData esClientConfigData;
    private final EsIndexerConfigData esIndexerConfigData;

    public ElasticModule() {
        this.esClientConfigData = JsonParser.fromJsonFile(ELASTICSEARCH_CONFIG_FILE_NAME, EsClientConfigData.class);
        this.esIndexerConfigData = JsonParser.fromJsonFile(ELASTIC_PRODUCER_CONFIG_FILE_NAME, EsIndexerConfigData.class);
    }

    @Provides
    public RestHighLevelClient getElasticClient() {
        return new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost(esClientConfigData.getElasticSearchContainerName(), esClientConfigData.getPort(), esClientConfigData.getScheme())));
    }

    @Provides
    public EsIndexerConfigData getEsIndexerConfigData() {
        return esIndexerConfigData;
    }
}
