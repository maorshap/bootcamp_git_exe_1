import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.*;

public class RestResourcesTest {
    private static Logger LOGGER = LoggerFactory.getLogger(RestResourcesTest.class.getName());
    private static String PRODUCER_URL = "http://localhost:8001/bootcamp/";
    private static String ACCOUNT_SERVICE_URL = "http://localhost:8090/account-service/";

    private static WebTarget getWebTarget(String url) {
        return ClientBuilder.newClient().target(url);
    }

    @Test
    public void testSearchWithoutIndexOfDocument(){
        //Creation of new Account
        String accountToken = createAccount();

        String generatedString = RandomStringUtils.random(15, true, false);
        String userAgent = "Macintosh";

        //Retrieve of the document from the account's elasticsearch index.
        await().atMost(15, TimeUnit.SECONDS).until(() -> isDocumentIndexed(generatedString, userAgent, accountToken));
    }

    @Test
    public void testIndexAndSearchOfDocument(){
        //Creation of new Account
        String accountToken = createAccount();

        String generatedString = RandomStringUtils.random(15, true, false);
        String userAgent = "Macintosh";

        //Index of the document to the account's elasticsearch index.
        Response indexResponse = insertDocumentIntoEs(generatedString, userAgent, accountToken);

        assertNotNull(indexResponse);
        assertTrue(indexResponse.getStatus() >= 200 && indexResponse.getStatus() < 300);
        LOGGER.debug("The message " + generatedString + " has been indexed successfully");

        //Retrieve of the document from the account's elasticsearch index.
        await().atMost(15, TimeUnit.SECONDS).until(() -> isDocumentIndexed(generatedString, userAgent, accountToken));
    }

    private String createAccount() {
        String jsonString = "{\"accountName\":\"" + "Kivid" + "\"}";
        Response searchResponse = getWebTarget(ACCOUNT_SERVICE_URL)
                .path("create-account")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(jsonString));

        Map<String, String> map = searchResponse.readEntity(Map.class);
        return map.get("token");
    }


    private Response insertDocumentIntoEs(String message, String userAgent, String token){
        String jsonString = "{\"message\":\"" + message + "\"}";
        String agentHeaderString = "Mozilla/5.0 (" + userAgent + "; Intel Mac OS X)";

        Response indexResponse = getWebTarget(PRODUCER_URL)
                .path("index")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.USER_AGENT, agentHeaderString)
                .header("X-ACCOUNT-TOKEN", token)
                .post(Entity.json(jsonString));

        return indexResponse;
    }

    private Response pullDocumentFromEs(String message, String userAgent, String token){
        Response searchResponse = getWebTarget(PRODUCER_URL).path("search")
                .queryParam("message", message)
                .queryParam("User-Agent", userAgent)
                .request(MediaType.APPLICATION_JSON)
                .header("X-ACCOUNT-TOKEN", token)
                .get();

        return searchResponse;
    }

    private boolean isDocumentIndexed(String message, String userAgent, String token){
        Response searchResponse = pullDocumentFromEs(message, userAgent, token);

        boolean isMessageIndexed = searchResponse.getStatus() >= 200 && searchResponse.getStatus() < 300;
        if(isMessageIndexed){
            LOGGER.debug("The retrived message from elasticsearch:\n" + searchResponse.readEntity(String.class));
        }

        return isMessageIndexed;
    }

}