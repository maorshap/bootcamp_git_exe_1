
import com.google.inject.Guice;
import com.google.inject.Injector;
import guice.modules.ServerModule;
import io.logz.guice.jersey.JerseyServer;


public class Main {
    public final static String JERSEY_REST_PACKAGE_PATH = "jersey/rest";

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new ServerModule(JERSEY_REST_PACKAGE_PATH));
        JerseyServer jerseyServer = injector.getInstance(JerseyServer.class);
        try {
            jerseyServer.start();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


}
