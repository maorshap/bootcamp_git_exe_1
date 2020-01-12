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
import utils.JsonParser;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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

    private final KafkaProducer<Integer, String> producer;
    private final ServerConfigData serverConfigData;
    private static int messageCounter = 1;

    @Inject
    public IndexResource(KafkaProducer<Integer, String> producer, ServerConfigData serverConfigData) {
        this.producer = requireNonNull(producer);
        this.serverConfigData = requireNonNull(serverConfigData);
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
    @Path("index/{token}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response indexDocument(DocumentMessage documentMessage, @HeaderParam("User-Agent") String userAgent, @PathParam("token") String token) {

        int responseStatus = HttpURLConnection.HTTP_INTERNAL_ERROR;
        StringBuilder sb = new StringBuilder();

        try {
            Map<String, Object> sourceToIndex = buildSourceMap(documentMessage, userAgent);
            Account account = getAccountFromDB(token);
            sourceToIndex.put("esIndexName", account.getEsIndexName().toLowerCase());
            String recordMsg = JsonParser.toJsonString(sourceToIndex);

            ProducerRecord producerRecord = new ProducerRecord(serverConfigData.getKafkaTopicName(), messageCounter, recordMsg);
            producer.send(producerRecord);

            responseStatus = HttpURLConnection.HTTP_ACCEPTED;
            sb.append("The message has been sent to kafka successfully.");
        }
        catch (InvalidMessageException e) {
            responseStatus = HttpURLConnection.HTTP_BAD_REQUEST;
            sb.append(e.getMessage());
        }
        catch (NoSuchAccountException e) {
            responseStatus = HttpURLConnection.HTTP_NO_CONTENT;
            sb.append(e.getMessage());
        }
        catch (Exception e) {
            sb.append("The message has not been sent to kafka successfully - error occurred.");
            e.printStackTrace();
        }
        finally {
            producer.flush();
            //producer.close();
            return Response.status(responseStatus)
                    .entity(sb.toString())
                    .build();
        }
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
        Optional<Account> optionalAccount = AccountServiceClient.getAccountFromDB(token);
        if (!optionalAccount.isPresent()) {
            throw new NoSuchAccountException("There is no such account with the given token in the database");
        }
        return optionalAccount.get();
    }

}
