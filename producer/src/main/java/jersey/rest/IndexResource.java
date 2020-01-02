package jersey.rest;

import boundaries.DocumentMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import entites.ServerConfigData;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

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

import static java.util.Objects.requireNonNull;

@Singleton
@Path("bootcamp")
public class IndexResource {

    private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final KafkaProducer<Integer, String> producer;
    private final ServerConfigData serverConfigData;
    private static int messageCounter = 1;

    @Inject
    public IndexResource(KafkaProducer<Integer, String> producer, ServerConfigData serverConfigData){
        this.producer = requireNonNull(producer);
        this.serverConfigData = requireNonNull(serverConfigData);
    }

    /**
     * "/index" entry point.
     *
     * @param documentMessage - Message content to be index
     * @param userAgent - Send by agent
     * @return Response invoked by Index action
     */
    @POST
    @Path("index")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response indexDocument(DocumentMessage documentMessage, @HeaderParam("User-Agent") String userAgent) {

        int responseStatus = HttpURLConnection.HTTP_INTERNAL_ERROR;
        StringBuilder sb = new StringBuilder();

        Map<String, Object> sourceToIndex = buildSourceMap(documentMessage, userAgent);

        if (sourceToIndex == null) {
            return Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
                    .entity("The message body is empty.")
                    .build();
        }

        try {
            //String recordMsg = requireNonNull(JsonParser.parseToJsonString(sourceToIndex));
            String recordMsg = OBJECT_MAPPER.writeValueAsString(sourceToIndex);
            ProducerRecord producerRecord = new ProducerRecord(serverConfigData.getKafkaTopicName(), messageCounter, recordMsg);
            producer.send(producerRecord);
            responseStatus = HttpURLConnection.HTTP_ACCEPTED;
            sb.append("The message has been sent to kafka successfully.");
        }
        catch (Exception e) {
            sb.append("The message has not been sent to kafka successfully - error occurred.");
            e.printStackTrace();
        }
        finally{
            producer.flush();
            return Response.status(responseStatus)
                    .entity(sb.toString())
                    .build();
        }

    }

    private Map<String, Object> buildSourceMap(DocumentMessage documentMessage, String userAgent) {
        String message = documentMessage.getMessage();
        if (!checkStringsValidation(message, userAgent)) {
            return null;
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

}
