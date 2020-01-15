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

import java.net.HttpURLConnection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.*;

public class RestResourcesTest {

    private final static int TOKEN_LENGTH = 32;
    private static Logger LOGGER = LoggerFactory.getLogger(RestResourcesTest.class.getName());
    private static String PRODUCER_URL = "http://localhost:8001/bootcamp/";
    private static String ACCOUNT_SERVICE_URL = "http://localhost:8090/account-service/";
    private static String ACCOUNT_TOKEN_HEADER = "X-ACCOUNT-TOKEN";
    private static String MESSAGE_QUERY_PARAMETER = "message";
    private static String USER_AGENT_QUERY_PARAMETER = "User-Agent";



    @Test
    public void testPassWrongTokenOnIndex(){
        String accountToken = RandomStringUtils.random(TOKEN_LENGTH, true, false);
        String generatedString = RandomStringUtils.random(15, true, false);
        String userAgent = "Macintosh";

        Response indexResponse = insertDocumentIntoEs(generatedString, userAgent, accountToken);

        assertNotNull(indexResponse);
        assertTrue(indexResponse.getStatus() == HttpURLConnection.HTTP_UNAUTHORIZED);
    }

    @Test
    public void testPassWrongTokensOnSearch(){
        String accountToken = RandomStringUtils.random(TOKEN_LENGTH, true, false);
        String generatedString = RandomStringUtils.random(15, true, false);
        String userAgent = "Macintosh";

        //We can assume that there is no account with the token accountToken value.
        await().atMost(15, TimeUnit.SECONDS).until(() -> isAccountNotFound(generatedString, userAgent, accountToken));
    }

    @Test
    public void testSearchWithoutIndexOfDocument() {

        String accountToken = createAccount();

        String generatedString = RandomStringUtils.random(15, true, false);
        String userAgent = "Macintosh";

        //Retrieve of the document from the account's elasticsearch index.
        await().atMost(15, TimeUnit.SECONDS).until(() -> isResultListEmpty(generatedString, userAgent, accountToken));
    }

    @Test
    public void testIndexAndSearchOfDocument() {

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

    private static WebTarget getWebTarget(String url) {
        return ClientBuilder.newClient().target(url);
    }


    private String createAccount() {
        CreateAccountRequest accountRequest = new CreateAccountRequest("Kivid");
        String jsonString = "{\"accountName\":\"" + "Kivid" + "\"}";
        Response searchResponse = getWebTarget(ACCOUNT_SERVICE_URL)
                .path("create-account")
                .request(MediaType.APPLICATION_JSON)
                //.post(Entity.json(jsonString));
        .post(Entity.json(accountRequest));

        Map<String, String> map = searchResponse.readEntity(Map.class);
        return map.get("token");
    }


    private Response insertDocumentIntoEs(String message, String userAgent, String token) {
        String jsonString = "{\"message\":\"" + message + "\"}";
        String agentHeaderString = "Mozilla/5.0 (" + userAgent + "; Intel Mac OS X)";

        Response indexResponse = getWebTarget(PRODUCER_URL)
                .path("index")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.USER_AGENT, agentHeaderString)
                .header(ACCOUNT_TOKEN_HEADER, token)
                .post(Entity.json(jsonString));

        return indexResponse;
    }

    private Response pullDocumentFromEs(String message, String userAgent, String token) {
        Response searchResponse = getWebTarget(PRODUCER_URL).path("search")
                .queryParam(MESSAGE_QUERY_PARAMETER, message)
                .queryParam(USER_AGENT_QUERY_PARAMETER, userAgent)
                .request(MediaType.APPLICATION_JSON)
                .header(ACCOUNT_TOKEN_HEADER, token)
                .get();

        return searchResponse;
    }

    private boolean isDocumentIndexed(String message, String userAgent, String token) {
        Response searchResponse = pullDocumentFromEs(message, userAgent, token);
        int responseStatus = searchResponse.getStatus();
        boolean isMessageIndexed = (responseStatus >= 200 && responseStatus < 300) && responseStatus != HttpURLConnection.HTTP_NO_CONTENT;
        if (isMessageIndexed) {
            LOGGER.debug("The retrived message from elasticsearch:\n" + searchResponse.readEntity(String.class));
        }

        return isMessageIndexed;
    }

    private static class CreateAccountRequest {

        private String accountName;

        public CreateAccountRequest(String accountName) {
            this.accountName = accountName;
        }

        public String getAccountName() {
            return accountName;
        }
    }

    private boolean isResultListEmpty(String message, String userAgent, String token) {
        Response searchResponse = pullDocumentFromEs(message, userAgent, token);
        return searchResponse.getStatus() == HttpURLConnection.HTTP_NO_CONTENT;
    }

    private boolean isAccountNotFound(String message, String userAgent, String token){
        Response searchResponse = pullDocumentFromEs(message, userAgent, token);
        return searchResponse.getStatus() == HttpURLConnection.HTTP_UNAUTHORIZED;
    }

}