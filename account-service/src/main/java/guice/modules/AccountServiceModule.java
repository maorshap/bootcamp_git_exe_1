package guice.modules;

import com.google.inject.AbstractModule;
import configuration.util.ConfigurationLoader;
import entities.JerseyConfigData;

public class AccountServiceModule extends AbstractModule {

    public final static String JERSEY_CONFIG_FILE_NAME = "jersey_server.config";
    private final JerseyConfigData jerseyConfigEntity;

    public AccountServiceModule(){
        this.jerseyConfigEntity = ConfigurationLoader.load(JERSEY_CONFIG_FILE_NAME, JerseyConfigData.class);
    }

    @Override
    protected void configure() {
        install(new JerseyServerModule(jerseyConfigEntity.getPackagePath(), jerseyConfigEntity.getPort()));
        install(new MyBatisAccountModule());
    }
}
