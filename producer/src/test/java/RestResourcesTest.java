import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Random;

import static org.junit.Assert.*;

public class RestResourcesTest {
    private static final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String CHAR_UPPER = CHAR_LOWER.toUpperCase();
    private static final String NUMBER = "0123456789";
    private static final String DATA_FOR_RANDOM_STRING = CHAR_LOWER + CHAR_UPPER + NUMBER;

    private static SecureRandom random = new SecureRandom();
    private static String BASE_URL = "http://localhost:8001/bootcamp/";

    private static WebTarget getWebTarget() {
        return ClientBuilder.newClient().target(BASE_URL);
    }

    @Test
    public void testSearchDocument() {
        String message = "boot";
        String userAgent = "Macintosh";

        Response response = getWebTarget().path("search")
                .queryParam("message", message)
                .queryParam("User-Agent", userAgent)
                .request(MediaType.APPLICATION_JSON)
                .get();

        assertTrue(response.getStatus() >= 200 && response.getStatus() < 300);

        System.out.println(response.readEntity(String.class));
    }

    @Test
    public void testIndexDocument() {
        String jsonString = "{\"message\":\"boot\"}";
        String agentHeaderString = "Mozilla/5.0 (Macintosh; Intel Mac OS X)";

        Response response = getWebTarget().path("index")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.USER_AGENT, agentHeaderString)
                .post(Entity.json(jsonString));

        assertNotNull(response);
        assertTrue(response.getStatus() >= 200 && response.getStatus() < 300);

        System.out.println(response.getStatus());

    }

    @Test
    public void testIndexAndSearchOfDocument() throws InterruptedException {

        String generatedString = generateRandomString(4);
        String jsonString = "{\"message\":\"" + generatedString + "\"}";
        String agentHeaderString = "Mozilla/5.0 (Macintosh; Intel Mac OS X)";

        Response indexResponse = getWebTarget().path("index")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.USER_AGENT, agentHeaderString)
                .post(Entity.json(jsonString));

        assertNotNull(indexResponse);
        assertTrue(indexResponse.getStatus() >= 200 && indexResponse.getStatus() < 300);

        System.out.println("The message " + generatedString + " has been indexed successfully");

        //TODO::To change to the wait util library implementation.
        Thread.sleep(1000 * 7);
        String userAgent = "Macintosh";

        System.out.println("User-Agent: " + userAgent);
        Response searchResponse = getWebTarget().path("search")
                .queryParam("message", generatedString)
                .queryParam("User-Agent", userAgent)
                .request(MediaType.APPLICATION_JSON)
                .get();

        assertNotNull(indexResponse);
        System.out.println(searchResponse.getStatus());
        assertTrue(searchResponse.getStatus() >= 200 && searchResponse.getStatus() < 300);

        System.out.println("The retrived message from elasticsearch:\n" + searchResponse.readEntity(String.class));
    }

    public static String generateRandomString(int length) {
        if (length < 1) throw new IllegalArgumentException();

        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int rndCharAt = random.nextInt(DATA_FOR_RANDOM_STRING.length());
            char rndChar = DATA_FOR_RANDOM_STRING.charAt(rndCharAt);

            sb.append(rndChar);
        }
        return sb.toString();

    }

}