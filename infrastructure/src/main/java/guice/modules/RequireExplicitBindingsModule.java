package guice.modules;

import com.google.inject.AbstractModule;

public class RequireExplicitBindingsModule extends AbstractModule {
    @Override
    protected void configure() {
        binder().requireExplicitBindings();
    }
}
