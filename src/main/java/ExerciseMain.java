import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
public class ExerciseMain {

    private static int counter = 1;

    public static void main(String[] args) {
        createHttpServer();
    }

    private static void createHttpServer(){
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8001), 0);
            server.createContext("/boot-bootcamp", new MyRequestHandler());
            server.setExecutor(null); // creates a default executor
            server.start();
        }catch(IOException e){
            System.out.println(e.getMessage());
        }

    }

    static class MyRequestHandler implements HttpHandler{

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String response = "This is the response";
            httpExchange.sendResponseHeaders(200, response.length());
            Logger logger = LogManager.getLogger(ExerciseMain.class);
            logger.info("boot boot" + counter++);
            OutputStream os = httpExchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}
