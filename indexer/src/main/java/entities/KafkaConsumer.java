package entities;

import Interfaces.MessageIndexer;
import com.google.inject.Inject;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import static java.util.Objects.requireNonNull;

public class KafkaConsumer {
    private final static int CONSUMER_POLL_TIMEOUT = 1000;
    private static Logger LOGGER = LoggerFactory.getLogger(KafkaConsumer.class);
    private final Consumer<Integer, String> consumer;
    private final MessageIndexer messageIndexer;

    @Inject
    public KafkaConsumer(Consumer<Integer, String> consumer, MessageIndexer messageIndexer) {
        this.consumer = requireNonNull(consumer);
        this.messageIndexer = requireNonNull(messageIndexer);
    }

    /**
     * Continues pull of record from Kafka broker.
     */
    public void pullRecords() {
        while (true) {
            ConsumerRecords<Integer, String> consumerRecords = consumer.poll(CONSUMER_POLL_TIMEOUT);

            consumerRecords.forEach(record -> {
                LOGGER.debug("Record Key " + record.key());
                LOGGER.debug("Record value " + record.value());
                LOGGER.debug("Record partition " + record.partition());
                LOGGER.debug("Record offset " + record.offset());

                handleRecord(record);
            });

            // commits the offset of record to broker.
            consumer.commitAsync();
        }
    }

    private void handleRecord(ConsumerRecord<Integer, String> record) {
        messageIndexer.indexMessage(record.value());
    }
}
