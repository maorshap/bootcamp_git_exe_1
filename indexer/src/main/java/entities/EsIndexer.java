package entities;

import Interfaces.MessageIndexer;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    public EsIndexer(RestHighLevelClient elasticClient, EsIndexerConfigData elasticProducer){
        this.elasticClient = requireNonNull(elasticClient);
        this.elasticProducer = requireNonNull(elasticProducer);
    }

    public void indexMessage(String message){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String, String> jsonMap = objectMapper.readValue(message, Map.class);

            IndexRequest indexRequest = new IndexRequest(elasticProducer.getIndexName(), elasticProducer.getDocumentType());
            indexRequest.source(jsonMap);

            IndexResponse indexResponse = elasticClient.index(indexRequest, RequestOptions.DEFAULT);
            LOGGER.debug("Index Response status: " + indexResponse.status().getStatus());
            LOGGER.debug("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


}
