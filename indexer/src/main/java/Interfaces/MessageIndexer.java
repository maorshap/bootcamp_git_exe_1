package Interfaces;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.elasticsearch.action.bulk.BulkRequest;

import java.util.Map;

public interface MessageIndexer {
    void indexMessage(String indexName, Map<String, String> map);
    void addRequestToBulk(BulkRequest bulk, ConsumerRecord<String, String> record);
    void indexBulkRequest(BulkRequest bulkRequest);
}
