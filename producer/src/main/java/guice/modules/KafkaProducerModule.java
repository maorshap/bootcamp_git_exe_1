package guice.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import configuration.ConfigurationLoader;
import entites.ProducerConfigEntity;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import parsers.ProducerConfigParser;

import java.util.Properties;

public class KafkaProducerModule extends AbstractModule {

    public final static String PRODUCER_CONFIG_FILE_NAME = "producer.config";
    private final ProducerConfigEntity producerConfigEntity;

    public KafkaProducerModule(){
        this.producerConfigEntity = ConfigurationLoader.load(PRODUCER_CONFIG_FILE_NAME, ProducerConfigEntity.class);
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
