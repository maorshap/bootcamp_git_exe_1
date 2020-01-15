package jersey.rest;

import Exceptions.InvalidMessageException;
import Exceptions.NoSuchAccountException;
import boundaries.DocumentMessage;

import javax.inject.Inject;
import javax.inject.Singleton;

import clients.AccountServiceClient;
import entities.Account;
import entities.ServerConfigData;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.JsonParser;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

@Singleton
@Path("bootcamp")
public class IndexResource {

    private static final String ACCOUNT_SERVICE_URL = "http://account_service:8090/account-service";

    private static Logger LOGGER = LogManager.getLogger(IndexResource.class);
    private static int messageCounter = 1;

    private final KafkaProducer<String, String> producer;
    private final ServerConfigData serverConfigData;
    private final AccountServiceClient accountServiceClient;

    @Inject
    public IndexResource(KafkaProducer<String, String> producer, ServerConfigData serverConfigData) {
        this.producer = requireNonNull(producer);
        this.serverConfigData = requireNonNull(serverConfigData);
        this.accountServiceClient = new AccountServiceClient(ACCOUNT_SERVICE_URL);
    }

    /**
     * <p>"/index" entry point.</p>
     * <p>Index Document into Kafka cluster.</p>
     *
     * @param documentMessage - Message content to be index.
     * @param userAgent       - Send by agent
     * @return Response invoked by Index action
     */
    @POST
    @Path("index")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response indexDocument(DocumentMessage documentMessage, @HeaderParam("User-Agent") String userAgent, @HeaderParam("X-ACCOUNT-TOKEN") String token) {
        try {
            Map<String, Object> sourceToIndex = buildSourceMap(documentMessage, userAgent);
            String recordMsg = JsonParser.toJsonString(sourceToIndex);

            Account account = getAccountFromDB(token);
            String esIndexName = account.getEsIndexName().toLowerCase();

            ProducerRecord producerRecord = new ProducerRecord(serverConfigData.getKafkaTopicName(), esIndexName, recordMsg);
            producer.send(producerRecord);
        }
        catch (InvalidMessageException e) {
            producer.flush();
            LOGGER.error(e.getMessage());
            return RestUtils.buildResponse(HttpURLConnection.HTTP_BAD_REQUEST, e.getMessage());
        }
        catch (NoSuchAccountException e) {
            producer.flush();
            LOGGER.error(e.getMessage());
            return RestUtils.buildResponse(HttpURLConnection.HTTP_UNAUTHORIZED, e.getMessage());
        }
        catch (Exception e) {
            producer.flush();
            LOGGER.error(e.getMessage());
            return RestUtils.buildResponse(HttpURLConnection.HTTP_INTERNAL_ERROR, "The message has not been sent to kafka successfully - error occurred.");
        }

        producer.flush();
        //producer.close();
        return RestUtils.buildResponse(HttpURLConnection.HTTP_ACCEPTED, "The message has been sent to kafka successfully.");

    }


    private Map<String, Object> buildSourceMap(DocumentMessage documentMessage, String userAgent) throws InvalidMessageException {
        String message = documentMessage.getMessage();
        if (!checkStringsValidation(message, userAgent)) {
            throw new InvalidMessageException("The message body is empty");
        }

        Map<String, Object> jsonAsMap = new HashMap<>();
        jsonAsMap.put("message", message + " " + messageCounter++);
        jsonAsMap.put("User-Agent", userAgent);

        return jsonAsMap;
    }

    private boolean checkStringsValidation(String... strings) {
        for (String str : strings) {
            if (str == null || str.trim().length() == 0)
                return false;
        }
        return true;
    }

    private Account getAccountFromDB(String token) throws NoSuchAccountException {
        Optional<Account> optionalAccount = accountServiceClient.getAccountFromDB(token);
        if (!optionalAccount.isPresent()) {
            throw new NoSuchAccountException("There is no such account with the given token in the database");
        }
        return optionalAccount.get();
    }


}
