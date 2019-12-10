package jettyrespackage;

import Dao.ServerConfiguration;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.logz.guice.jersey.JerseyServer;


public class ExerciseMain {

    public final static String PACKAGE_PATH = "jettyrespackage";

    public static void main(String[] args) {
        ServerConfiguration serverConfiguration = ServerConfigurationParser.parse("server.config");
        Injector injector = Guice.createInjector(new ServerModule(PACKAGE_PATH, serverConfiguration));
        JerseyServer jerseyServer = injector.getInstance(JerseyServer.class);
        try {
            jerseyServer.start();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


}
