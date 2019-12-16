import com.google.inject.Guice;
import com.google.inject.Injector;
import entities.ConsumerEntity;
import entities.IndexerEntity;
import guice.modules.MainModule;

public class Main {

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new MainModule());

        IndexerEntity indexerEntity = injector.getInstance(IndexerEntity.class);
        ConsumerEntity consumerEntity = injector.getInstance(ConsumerEntity.class);

        consumerEntity.setIndexer(indexerEntity);
        consumerEntity.pullRecords();

    }


}
