package jettyrespackage;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.logz.guice.jersey.JerseyServer;


public class ExerciseMain {

    public final static String PACKAGE_PATH = "jettyrespackage";

    public static void main(String[] args) {
        //createHttpServer();
        //createJettyServer();
        Injector injector = Guice.createInjector(new ServerModule(PACKAGE_PATH));
        JerseyServer jerseyServer = injector.getInstance(JerseyServer.class);
        try {
            jerseyServer.start();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

   /* private static void createJettyServer() {
        //Injector which calls getInstance of ServletContextHandler
        ResourceConfig config = new ResourceConfig();
        config.packages("jettyrespackage");
        ServletHolder servlet = new ServletHolder(new ServletContainer(config));


        Server server = new Server(PORT);
        ServletContextHandler context = new ServletContextHandler(server, "/*");
        context.addServlet(servlet, "/*");

        try {
            server.start();
            server.join();
        } catch(Exception e){
            System.out.println(e.getMessage());
        }  finally{
            server.destroy();
        }
    }
    */

  /*  private static void createHttpServer() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
            server.createContext("/boot-bootcamp", ExerciseMain::handle);
            server.setExecutor(null); // creates a default executor
            server.start();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }*/

  /* public static void handle(HttpExchange httpExchange) throws IOException {
        String response = "boot boot " + visit_counter++;
        httpExchange.sendResponseHeaders(200, response.length());
        Logger logger = LogManager.getLogger(ExerciseMain.class);
        logger.info(response);
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }*/
}
