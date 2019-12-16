package guice.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import entities.IndexerEntity;

public class MainModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new KafkaConsumerModule());
        install(new ElasticModule());
    }

}
