package entities;

import Interfaces.MessageIndexer;
import com.google.inject.Inject;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;

import static java.util.Objects.requireNonNull;

public class EsIndexer implements MessageIndexer {
    private static Logger LOGGER = LoggerFactory.getLogger(EsIndexer.class.getName());
    private final RestHighLevelClient elasticClient;
    private final EsIndexerConfigData elasticProducer;

    @Inject
    public EsIndexer(RestHighLevelClient elasticClient, EsIndexerConfigData elasticProducer) {
        this.elasticClient = requireNonNull(elasticClient);
        this.elasticProducer = requireNonNull(elasticProducer);
    }

    public void indexMessage(String indexName, Map<String, String> jsonMap) {

        IndexRequest indexRequest = new IndexRequest(indexName, elasticProducer.getDocumentType());
        indexRequest.source(jsonMap);

        try {
            IndexResponse indexResponse = elasticClient.index(indexRequest, RequestOptions.DEFAULT);
            LOGGER.debug("Index Response status: " + indexResponse.status().getStatus());
            LOGGER.debug("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        }
        catch (Exception e) {
            StringBuilder sb = new StringBuilder();
            sb.append("Error at indexing of the message: ")
                    .append(jsonMap.get("message"))
                    .append("\nto Index: ")
                    .append(indexName);

            LOGGER.debug(sb.toString());
            e.printStackTrace();
        }


    }


}
