package guice.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import configuration.util.ConfigurationLoader;
import entities.ConsumerConfigEntity;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Collections;
import java.util.Properties;

public class KafkaConsumerModule extends AbstractModule {

    public final static String CONSUMER_CONFIG_FILE_NAME = "consumer.config";
    private final ConsumerConfigEntity consumerConfigEntity;

    public KafkaConsumerModule(){
        this.consumerConfigEntity = ConfigurationLoader.load(CONSUMER_CONFIG_FILE_NAME, ConsumerConfigEntity.class);
    }

    @Provides
    public Consumer<Integer, String> buildConsumer(){
        Properties props = new Properties();
        StringBuilder sb = new StringBuilder();
        sb.append(consumerConfigEntity.getHost())
                .append(":")
                .append(consumerConfigEntity.getPort());

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, sb.toString());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerConfigEntity.getGroupId());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

        Consumer<Integer, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(consumerConfigEntity.getTopic()));
        return consumer;
    }
}
