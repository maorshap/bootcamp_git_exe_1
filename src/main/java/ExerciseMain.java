import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class ExerciseMain {
    public static void main(String[] args) {

    }

    private void createHttpServer() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000),0);
        server.createContext("/boot-bootcamp", new MyRequestHandler());

    }

    class MyRequestHandler implements HttpHandler{

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String response = "This is the response";
            httpExchange.sendResponseHeaders(200, response.length());
            OutputStream os = httpExchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}
