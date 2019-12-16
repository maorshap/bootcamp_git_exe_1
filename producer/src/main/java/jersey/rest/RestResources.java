package jersey.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import entites.ServerConfigEntity;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import entites.DocumentMessageEntity;
import entites.ShipResponseEntity;

import static java.util.Objects.requireNonNull;

@Path("bootcamp")
public class RestResources {
    public final static String TOPIC = "topic1";

    private final static String INDEX_NAME = "messages";
    private final static String DOCUMENT_TYPE = "type1";

    private static int messageCounter = 1;

    //Injected Objects
    private final ServerConfigEntity serverConfigEntity;
    private final RestHighLevelClient elasticsearchClient;
    private final KafkaProducer<Integer, String> producer;


    @Inject
    public RestResources(ServerConfigEntity serverConfigEntity, KafkaProducer<Integer, String> kafkaProducer, RestHighLevelClient elasticsearchClient) {
        this.serverConfigEntity = requireNonNull(serverConfigEntity);
        this.producer = requireNonNull(kafkaProducer);
        this.elasticsearchClient = requireNonNull(elasticsearchClient);
    }


    /**
     * "/ship" entry point.
     *
     * @return Response
     */
    @GET
    @Path("ship")
    @Produces(MediaType.APPLICATION_JSON)
    public Response shipLog() {
        ShipResponseEntity response = new ShipResponseEntity();
        response.setMessage(serverConfigEntity.getLogMessage());
        response.setCounter(messageCounter++);

        Logger logger = LogManager.getLogger(RestResources.class);
        logger.info(response);

        return Response
                .status(Response.Status.OK)
                .entity(response)
                .build();
    }


    /**
     * "/search" entry point.
     *
     * @param uriInfo TCP context
     * @return Response invoke by Search
     */
    @GET
    @Path("search")
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchDocument2(@Context UriInfo uriInfo) {
        SearchRequest searchRequest = buildSearchRequest(uriInfo);
        return buildResponse(searchRequest);
    }


    /**
     * "/search/v2" entry point.
     * Used for original assignment which requested url header query parameter.
     *
     * @param message content
     * @param header content
     * @return Response invoked by Search
     */
    @GET
    @Path("search/v2")
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchDocument(@QueryParam("message") String message, @QueryParam("header") String header) {
        SearchRequest searchRequest = new SearchRequest(INDEX_NAME);
        SearchSourceBuilder searchSourceBuilder = buildSearchQuery(message, header);
        searchRequest.source(searchSourceBuilder);
        return buildResponse(searchRequest);
    }


    /**
     * "/index" entry point.
     *
     * @param documentMessageEntity - Message content to be index
     * @param userAgent - Send by agent
     * @return Response invoked by Index action
     */
    @POST
    @Path("index")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response indexDocument(DocumentMessageEntity documentMessageEntity, @HeaderParam("User-Agent") String userAgent) {

        int responseStatus = HttpURLConnection.HTTP_INTERNAL_ERROR;
        StringBuilder sb = new StringBuilder();

        Map<String, Object> sourceToIndex = buildSourceMap(documentMessageEntity, userAgent);

        if (sourceToIndex == null) {
            return Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
                    .entity("The message body is empty.")
                    .build();
        }

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            String recordMsg = objectMapper.writeValueAsString(sourceToIndex);
            ProducerRecord producerRecord = new ProducerRecord(TOPIC, messageCounter, recordMsg);
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
            producer.close();
            return Response.status(responseStatus)
                    .entity(sb.toString())
                    .build();
        }

    }


    private boolean checkStringsValidation(String... strings) {
        for (String str : strings) {
            if (str == null || str.trim().length() == 0)
                return false;
        }
        return true;
    }


    private Map<String, Object> buildSourceMap(DocumentMessageEntity documentMessageEntity, String userAgent) {
        String message = documentMessageEntity.getMessage();
        if (!checkStringsValidation(message, userAgent)) {
            return null;
        }

        Map<String, Object> jsonAsMap = new HashMap<>();
        jsonAsMap.put("message", message + " " + messageCounter++);
        jsonAsMap.put("User-Agent", userAgent);

        return jsonAsMap;
    }


    private SearchRequest buildSearchRequest(UriInfo uriInfo) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
        for (Map.Entry<String, List<String>> param : queryParams.entrySet()) {
            QueryBuilder curQuery = QueryBuilders.matchQuery(param.getKey(), param.getValue().iterator().next());
            boolQueryBuilder.must(curQuery);
        }

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(boolQueryBuilder);

        SearchRequest searchRequest = new SearchRequest(INDEX_NAME);
        searchRequest.source(searchSourceBuilder);

        return searchRequest;
    }


    private Response buildResponse(SearchRequest request){
        StringBuilder sb = new StringBuilder();
        int responseStatus = HttpURLConnection.HTTP_INTERNAL_ERROR;

        try{
            SearchResponse searchResponse = elasticsearchClient.search(request, RequestOptions.DEFAULT);
            responseStatus = HttpURLConnection.HTTP_OK;

            SearchHits searchHits = searchResponse.getHits();
            for (SearchHit hit : searchHits)
                sb.append(hit).append("\n");
        }
        catch(IOException e){
            sb.append("Internal error has occurred.");
            e.printStackTrace();
        }
        finally {
            return Response.status(responseStatus)
                    .entity(sb.toString())
                    .build();
        }

    }


    private SearchSourceBuilder buildSearchQuery(String message, String header) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.boolQuery()
                .must(QueryBuilders.matchQuery("message", message))
                .must(QueryBuilders.matchQuery("User-Agent", header)));
        return searchSourceBuilder;
    }


    private Response buildResponse(IndexRequest request){
        StringBuilder sb = new StringBuilder();
        int responseStatus = HttpURLConnection.HTTP_INTERNAL_ERROR;

        try{
            IndexResponse indexResponse = elasticsearchClient.index(request, RequestOptions.DEFAULT);
            responseStatus = indexResponse.status().getStatus();
            sb.append("Document has been indexed successfully.");
        }
        catch(IOException e){
            sb.append("Internal error has occurred.");
            e.printStackTrace();
        }
        finally {
            return Response.status(responseStatus)
                    .entity(sb.toString())
                    .build();
        }

    }

}

