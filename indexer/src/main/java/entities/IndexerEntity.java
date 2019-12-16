package entities;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;

import java.util.Map;

import static java.util.Objects.requireNonNull;

public class IndexerEntity {
    private final RestHighLevelClient elasticClient;
    private final ElasticProducerConfigEntity elasticProducer;

    @Inject
    public IndexerEntity(RestHighLevelClient elasticClient, ElasticProducerConfigEntity elasticProducer){
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
            System.out.println("Index Response status: " + indexResponse.status().getStatus());
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


}
