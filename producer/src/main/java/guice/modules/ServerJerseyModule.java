package guice.modules;

import com.google.inject.AbstractModule;
import io.logz.guice.jersey.JerseyModule;
import io.logz.guice.jersey.configuration.JerseyConfiguration;

public class ServerJerseyModule extends AbstractModule {

    private final JerseyConfiguration jerseyConfiguration;

    public ServerJerseyModule(String packagePath, int port){
      this.jerseyConfiguration = buildJerseyConfiguarion(packagePath, port);
    }

    @Override
    protected void configure() {
        install(new JerseyModule(jerseyConfiguration));
    }

    private JerseyConfiguration buildJerseyConfiguarion(String path, int port){
        return JerseyConfiguration.builder()
                .addPackage(path)
                .addPort(port)
                .build();
    }
}
