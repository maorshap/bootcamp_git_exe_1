import com.google.inject.Guice;
import com.google.inject.Injector;
import entities.KafkaConsumer;
import guice.modules.IndexerModule;
import guice.modules.RequireExplicitBindingsModule;

public class IndexerMain {

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new IndexerModule(),  new RequireExplicitBindingsModule());
        KafkaConsumer kafkaConsumer = injector.getInstance(KafkaConsumer.class);

        kafkaConsumer.pullRecords();
    }


}

