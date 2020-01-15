import com.google.inject.Guice;
import com.google.inject.Injector;
import guice.modules.AccountServiceModule;
import guice.modules.RequireExplicitBindingsModule;
import io.logz.guice.jersey.JerseyServer;

public class AccountServiceMain {
    public static void main(String[] args){
        Injector injector = Guice.createInjector(new AccountServiceModule(), new RequireExplicitBindingsModule());
        JerseyServer jerseyServer = injector.getInstance(JerseyServer.class);
        try {
            jerseyServer.start();
        }
        catch (Exception e) {
           e.printStackTrace();
        }
    }
}
