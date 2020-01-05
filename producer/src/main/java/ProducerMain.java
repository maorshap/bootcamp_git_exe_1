
import com.google.inject.Guice;
import com.google.inject.Injector;
import guice.modules.ProducerModule;
import io.logz.guice.jersey.JerseyServer;


public class ProducerMain {
    public final static String JERSEY_REST_PACKAGE_PATH = "jersey/rest";

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new ProducerModule(JERSEY_REST_PACKAGE_PATH));
        JerseyServer jerseyServer = injector.getInstance(JerseyServer.class);
        try {
            jerseyServer.start();
        }
        catch (Exception e) {
           e.printStackTrace();
        }
    }


}
