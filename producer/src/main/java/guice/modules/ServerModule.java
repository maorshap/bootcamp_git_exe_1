package guice.modules;

import configuration.ConfigurationLoader;
import entites.ServerConfigEntity;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import parsers.ServerConfigParser;

public class ServerModule extends AbstractModule {

    public final static String SERVER_CONFIG_FILE_NAME = "server.config";
    private final ServerConfigEntity serverConfigEntity;
    private final String packagePath;

    public ServerModule(String packagePath) {
        this.serverConfigEntity = ConfigurationLoader.load(SERVER_CONFIG_FILE_NAME, ServerConfigEntity.class);
        this.packagePath = packagePath;
    }

    @Override
    protected void configure() {
        install(new ServerJerseyModule(packagePath, serverConfigEntity.getPort()));
        install(new ElasticserachModule());
        install(new KafkaProducerModule());
    }

    @Provides
    public ServerConfigEntity getServerConfigEntity() {
        return this.serverConfigEntity;
    }

}

