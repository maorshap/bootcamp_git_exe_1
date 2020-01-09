package guice.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import entities.KafkaProducerConfigData;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import utils.JsonParser;

import java.util.Properties;

public class KafkaProducerModule extends AbstractModule {

    public final static String PRODUCER_CONFIG_FILE_NAME = "producer.config";
    private final KafkaProducerConfigData producerConfigEntity;

    public KafkaProducerModule(){
        this.producerConfigEntity = JsonParser.fromJsonFile(PRODUCER_CONFIG_FILE_NAME, KafkaProducerConfigData.class);
    }

    @Provides
    public KafkaProducer<Integer, String> buildKafkaProducer() {
        StringBuilder sb = new StringBuilder();
        sb.append(producerConfigEntity.getHost())
                .append(":")
                .append(producerConfigEntity.getPort());

        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, sb.toString());
        properties.put(ProducerConfig.CLIENT_ID_CONFIG, producerConfigEntity.getId());
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        return new KafkaProducer<>(properties);
    }
}
