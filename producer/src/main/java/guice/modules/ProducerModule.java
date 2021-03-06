package guice.modules;

import configuration.util.ConfigurationLoader;
import entites.ServerConfigData;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class ProducerModule extends AbstractModule {

    public final static String SERVER_CONFIG_FILE_NAME = "server.config";
    private final ServerConfigData serverConfigData;
    private final String packagePath;

    public ProducerModule(String packagePath) {
        this.serverConfigData = ConfigurationLoader.load(SERVER_CONFIG_FILE_NAME, ServerConfigData.class);
        this.packagePath = packagePath;
    }

    @Override
    protected void configure() {
        install(new ServerJerseyModule(packagePath, serverConfigData.getPort()));
        install(new ElasticserachModule());
        install(new KafkaProducerModule());
    }

    @Provides
    public ServerConfigData getServerConfigData() {
        return this.serverConfigData;
    }

}

