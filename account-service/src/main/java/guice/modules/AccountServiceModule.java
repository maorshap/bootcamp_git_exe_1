package guice.modules;

import com.google.inject.AbstractModule;
import entities.JerseyConfigData;
import utils.JsonParser;

public class AccountServiceModule extends AbstractModule {

    public final static String JERSEY_CONFIG_FILE_NAME = "jersey_server.config";
    private final JerseyConfigData jerseyConfigEntity;

    public AccountServiceModule(){
        this.jerseyConfigEntity = JsonParser.fromJsonFile(JERSEY_CONFIG_FILE_NAME, JerseyConfigData.class);
    }

    @Override
    protected void configure() {
        install(new JerseyServerModule(jerseyConfigEntity.getPackagePath(), jerseyConfigEntity.getPort()));
        install(new MyBatisAccountModule());
    }
}
