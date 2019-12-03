package jettyrespackage;

import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.junit.Assert.*;

public class RestResourcesTest {
    private static String BASE_URL = "http://localhost:8001/bootcamp/";

    private static WebTarget getWebTarget() {
        return ClientBuilder.newClient().target(BASE_URL);
    }

    @Test
    public void testSearchDocument() {
        String message = "boot";
        String header = "Macintosh";

        Response response = getWebTarget().path("search")
                .queryParam("message", message)
                .queryParam("header", header)
                .request(MediaType.APPLICATION_JSON)
                .get();

        System.out.println(response.readEntity(String.class));
        assertTrue(response.getStatus() >= 200 && response.getStatus() < 300);
    }

    @Test
    public void testIndexDocument() {
        String jsonString = "{\"message\":\"boot\"}";
        String agentHeaderString = "Mozilla/5.0 (Macintosh; Intel Mac OS X)";

        Response response = getWebTarget().path("index")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.USER_AGENT, agentHeaderString)
                .post(Entity.json(jsonString));


        System.out.println(response.getStatus());
        assertNotNull(response);

    }

}