package entities;

import com.google.inject.Inject;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import static java.util.Objects.requireNonNull;

public class ConsumerEntity {
    private final static int CONSUMER_POLL_TIMEOUT = 1000;
    private final Consumer<Integer, String> kafkaConsumer;
    private IndexerEntity indexerEntity;

    @Inject
    public ConsumerEntity(Consumer<Integer, String> kafkaConsumer){
        this.kafkaConsumer = requireNonNull(kafkaConsumer);
    }

    public void setIndexer(IndexerEntity indexerEntity){
        this.indexerEntity = requireNonNull(indexerEntity);
    }

    public void pullRecords(){
        while (true) {
            ConsumerRecords<Integer, String> consumerRecords = kafkaConsumer.poll(CONSUMER_POLL_TIMEOUT);

            //print each record.
            consumerRecords.forEach(record -> {
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                System.out.println("Record Key " + record.key());
                System.out.println("Record value " + record.value());
                System.out.println("Record partition " + record.partition());
                System.out.println("Record offset " + record.offset());
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

                handleRecord(record);
            });

            // commits the offset of record to broker.
            kafkaConsumer.commitAsync();
        }
    }

    private void handleRecord(ConsumerRecord<Integer, String> record){
        if(indexerEntity != null){
            indexerEntity.indexMessage(record.value());
        }
    }
}
