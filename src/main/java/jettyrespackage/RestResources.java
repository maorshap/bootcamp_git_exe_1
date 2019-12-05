package jettyrespackage;

import Dao.ServerConfiguration;
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

import Dao.DocumentMessage;
import Dao.ShipResponse;

@Path("bootcamp")
public class RestResources {
    private static int visit_counter = 1;
    private final static String INDEX_NAME = "messages";
    private final static String DOCUMENT_TYPE = "type1";

    //Injected Objects
    private final ServerConfiguration serverConfiguration;
    private final RestHighLevelClient elasticsearchClient;


    @Inject
    public RestResources(ServerConfiguration serverConfiguration, RestHighLevelClient elasticsearchClient) {
        this.serverConfiguration = serverConfiguration;
        this.elasticsearchClient = elasticsearchClient;
    }


    /**
     * "/ship" entry point.
     *
     * @return
     */
    @GET
    @Path("ship")
    @Produces(MediaType.APPLICATION_JSON)
    public Response shipLog() {
        ShipResponse response = new ShipResponse();
        response.setMessage(serverConfiguration.getLogMessage());
        response.setCounter(visit_counter++);
        // String response = serverConfiguration.getLogMessage() + visit_counter++;

        Logger logger = LogManager.getLogger(ExerciseMain.class);
        logger.info(response);

        return Response
                .status(Response.Status.OK)
                .entity(response)
                .build();
    }


    /**
     * "/search" entry point.
     *
     * @param uriInfo
     * @return
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
     * @param message
     * @param header
     * @return
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

    private SearchSourceBuilder buildSearchQuery(String message, String header) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.boolQuery()
                .must(QueryBuilders.matchQuery("message", message))
                .must(QueryBuilders.matchQuery("User-Agent", header)));
        return searchSourceBuilder;
    }


    /**
     * "/index" entry point.
     *
     * @param documentMessage
     * @param userAgent
     * @return
     */
    @POST
    @Path("index")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response indexDocument(DocumentMessage documentMessage, @HeaderParam("User-Agent") String userAgent) {

        Map<String, Object> sourceToIndex = buildSourceMap(documentMessage, userAgent);

        if (sourceToIndex == null) {
            return Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
                    .entity("The message body is empty.")
                    .build();
        }

        IndexRequest indexRequest = new IndexRequest(INDEX_NAME, DOCUMENT_TYPE);
        indexRequest.source(sourceToIndex);

        return buildResponse(indexRequest);
    }


    /**
     * @param strings
     * @return
     */
    private boolean checkStringsValidation(String... strings) {
        for (String str : strings) {
            if (str == null || str.trim().length() == 0)
                return false;
        }
        return true;
    }


    /**
     * @param documentMessage
     * @param userAgent
     * @return
     */
    private Map<String, Object> buildSourceMap(DocumentMessage documentMessage, String userAgent) {
        String message = documentMessage.getMessage();
        if (!checkStringsValidation(message, userAgent)) {
            return null;
        }

        Map<String, Object> jsonAsMap = new HashMap<>();
        System.out.println(documentMessage.getMessage() + ", " + userAgent);
        jsonAsMap.put("message", message);
        jsonAsMap.put("User-Agent", userAgent);

        return jsonAsMap;
    }


    /**
     * @param uriInfo
     * @return
     */
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


    /**
     * @param requestObject
     * @param <E>
     * @return
     */
    private <E> Response buildResponse(E requestObject) {

        int responseStatus = HttpURLConnection.HTTP_INTERNAL_ERROR;
        StringBuilder sb = new StringBuilder();

        try {
            if (requestObject instanceof IndexRequest) {
                IndexResponse indexResponse = elasticsearchClient.index((IndexRequest) requestObject, RequestOptions.DEFAULT);
                responseStatus = HttpURLConnection.HTTP_OK;
                sb.append("Document has been indexed successfully.");
            }
            else if (requestObject instanceof SearchRequest) {
                SearchResponse searchResponse = elasticsearchClient.search((SearchRequest) requestObject, RequestOptions.DEFAULT);
                responseStatus = HttpURLConnection.HTTP_OK;

                SearchHits searchHits = searchResponse.getHits();
                for (SearchHit hit : searchHits)
                    sb.append(hit).append("\n");
            }
            else {
                responseStatus = HttpURLConnection.HTTP_NOT_ACCEPTABLE;
                sb.append("Unrecognized request object.");
            }
        }
        catch (IOException e) {
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

