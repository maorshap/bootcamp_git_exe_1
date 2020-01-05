package guice.modules;

import com.google.inject.AbstractModule;
import io.logz.guice.jersey.JerseyModule;
import io.logz.guice.jersey.configuration.JerseyConfiguration;

public class JerseyServerModule extends AbstractModule {

    private final JerseyConfiguration jerseyConfiguration;

    public JerseyServerModule(String packagePath, int port){
        this.jerseyConfiguration = buildJerseyConfiguration(packagePath, port);
    }

    @Override
    protected void configure() {
        install(new JerseyModule(jerseyConfiguration));
    }

    private JerseyConfiguration buildJerseyConfiguration(String path, int port){
        return JerseyConfiguration.builder()
                .addPackage(path)
                .addPort(port)
                .build();
    }
}
