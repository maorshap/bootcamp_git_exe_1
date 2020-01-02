import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.*;

public class RestResourcesTest {
    private static Logger LOGGER = LoggerFactory.getLogger(RestResourcesTest.class.getName());
    private static String BASE_URL = "http://localhost:8001/bootcamp/";

    private static WebTarget getWebTarget() {
        return ClientBuilder.newClient().target(BASE_URL);
    }


    @Test
    public void testIndexAndSearchOfDocument() throws InterruptedException {

        String generatedString = RandomStringUtils.random(15, true, false);
        String userAgent = "Macintosh";

        Response indexResponse = insertDocumentIntoEs(generatedString, userAgent);
        assertNotNull(indexResponse);
        assertTrue(indexResponse.getStatus() >= 200 && indexResponse.getStatus() < 300);

        LOGGER.debug("The message " + generatedString + " has been indexed successfully");

        await().atMost(15, TimeUnit.SECONDS).until(() -> isDocumentIndexed(generatedString, userAgent));
    }


    private Response insertDocumentIntoEs(String message, String userAgent){
        String jsonString = "{\"message\":\"" + message + "\"}";
        String agentHeaderString = "Mozilla/5.0 (" + userAgent + "; Intel Mac OS X)";

        Response indexResponse = getWebTarget().path("index")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.USER_AGENT, agentHeaderString)
                .post(Entity.json(jsonString));

        return indexResponse;
    }

    private Response pullDocumentFromEs(String message, String userAgent){
        Response searchResponse = getWebTarget().path("search")
                .queryParam("message", message)
                .queryParam("User-Agent", userAgent)
                .request(MediaType.APPLICATION_JSON)
                .get();

        return searchResponse;
    }

    private boolean isDocumentIndexed(String message, String userAgent){
        Response searchResponse = pullDocumentFromEs(message, userAgent);

        boolean isMessageIndexed = searchResponse.getStatus() >= 200 && searchResponse.getStatus() < 300;
        if(isMessageIndexed){
            LOGGER.debug("The retrived message from elasticsearch:\n" + searchResponse.readEntity(String.class));
        }

        return isMessageIndexed;
    }

}