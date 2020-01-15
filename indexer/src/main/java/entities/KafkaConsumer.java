package entities;

import Interfaces.MessageIndexer;
import com.google.inject.Inject;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import org.elasticsearch.action.bulk.BulkRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.JsonParser;


import java.util.Map;

import static java.util.Objects.requireNonNull;

public class KafkaConsumer {

    private final static int CONSUMER_POLL_TIMEOUT = 1000;
    private final static Logger LOGGER = LoggerFactory.getLogger(KafkaConsumer.class);

    private final Consumer<String, String> consumer;
    private final MessageIndexer messageIndexer;


    @Inject
    public KafkaConsumer(Consumer<String, String> consumer, MessageIndexer messageIndexer) {
        this.consumer = requireNonNull(consumer);
        this.messageIndexer = requireNonNull(messageIndexer);
    }

    /**
     * Continues pull of records from Kafka broker.
     */
    public void pullRecords() {
        while (true) {
            ConsumerRecords<String, String> consumerRecords = consumer.poll(CONSUMER_POLL_TIMEOUT);
            if(consumerRecords.isEmpty()) continue;

            BulkRequest bulk = new BulkRequest();

            consumerRecords.forEach(record -> {
                LOGGER.debug("Current pulled Record data:");
                LOGGER.debug("  Record Key " + record.key());
                LOGGER.debug("  Record value " + record.value());
                LOGGER.debug("  Record partition " + record.partition());
                LOGGER.debug("  Record offset " + record.offset());

                messageIndexer.addRequestToBulk(bulk, record);
            });

            messageIndexer.indexBulkRequest(bulk);
            // commits the offset of record to broker.
            consumer.commitAsync();
        }
    }


    private void handleRecord(ConsumerRecord<String, String> record) {
        try {
            Map<String, String> jsonMap = JsonParser.fromJsonString(record.value(), Map.class);
            messageIndexer.indexMessage(record.key(), jsonMap);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }



}
