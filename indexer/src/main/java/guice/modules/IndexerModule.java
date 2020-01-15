package guice.modules;

import Interfaces.MessageIndexer;
import com.google.inject.AbstractModule;
import entities.EsIndexer;

public class IndexerModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new KafkaConsumerModule());
        install(new ElasticModule());

        bind(MessageIndexer.class).to(EsIndexer.class);

    }

}
