package jettyrespackage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.DocWriteResponse;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("bootcamp")
public class RestResources {
    private static int visit_counter = 1;
    private final static String INVERSE_INDEX_NAME = "messages";
    private final static String DOCUMENT_TYPE = "type_1";

    //Injected Objects
    private ServerConfiguration serverConfiguration;
    private RestHighLevelClient client;

    @Inject
    public RestResources(ServerConfiguration serverConfiguration, RestHighLevelClient client) {
        this.serverConfiguration = serverConfiguration;
        this.client = client;
    }

    //Method should be overloading when there are more than 1 Index request implementation.
    //Can be better implementation by making it to generic by use of Reflection api.
    private IndexRequest getIndexRequest(DocumentMessage documentMessage, String userAgent) {
        Map<String, String> map = new HashMap<>();
        map.put("message", documentMessage.getMessage());
        map.put("User-Agent", userAgent);

        return new IndexRequest(INVERSE_INDEX_NAME, DOCUMENT_TYPE).source(map);
    }

    //Generic generated search query exactly by the provided query parameters.
    private SearchRequest getSearchQuery(UriInfo uriInfo) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
        for (Map.Entry<String, List<String>> param : queryParams.entrySet()) {
            QueryBuilder curQuery = QueryBuilders.matchQuery(param.getKey(), param.getValue().iterator().next());
            boolQueryBuilder.must(curQuery);
        }

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(boolQueryBuilder);

        SearchRequest searchRequest = new SearchRequest(INVERSE_INDEX_NAME);
        searchRequest.source(searchSourceBuilder);

        return searchRequest;
    }

    @GET
    @Path("ship")
    @Produces(MediaType.TEXT_PLAIN)
    public Response shipLog() {
        String response = serverConfiguration.getLogMessage() + visit_counter++;
        Logger logger = LogManager.getLogger(ExerciseMain.class);
        logger.info(response);

        return Response
                .status(Response.Status.OK)
                .entity(response)
                .build();
    }

    @GET
    @Path("search")
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchDocument2(@Context UriInfo uriInfo) {

        SearchRequest searchRequest = getSearchQuery(uriInfo);
        SearchResponse searchResponse;
        StringBuilder sb = new StringBuilder();

        try {
            searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits hits = searchResponse.getHits();
            for (SearchHit hit : hits) {
                sb.append(hit.toString()).append("\n");
            }
        } catch (IOException e) {
            sb.append("Failed in search:(");
            e.printStackTrace();
        } finally {
            return Response
                    .status(Response.Status.OK)
                    .entity(sb.toString())
                    .build();
        }
    }

    //Used for original asked requested url query parameter (header)
    @GET
    @Path("search/v2")
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchDocument(@QueryParam("message") String message, @QueryParam("header") String header) {

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.boolQuery()
                .must(QueryBuilders.matchQuery("message", message))
                .must(QueryBuilders.matchQuery("User-Agent", header)));


        SearchRequest searchRequest = new SearchRequest(INVERSE_INDEX_NAME);
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse;
        StringBuilder sb = new StringBuilder();

        try {
            searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits hits = searchResponse.getHits();
            for (SearchHit hit : hits) {
                sb.append(hit.toString()).append("\n");
            }
        } catch (IOException e) {
            sb.append("Failed in search:(");
            e.printStackTrace();
        } finally {
            return Response
                    .status(Response.Status.OK)
                    .entity(sb.toString())
                    .build();
        }
    }

    @POST
    @Path("index")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response indexDocument(DocumentMessage documentMessage, @HeaderParam("User-Agent") String userAgent) {

        //checking Edge case
        if (documentMessage.getMessage().trim().length() == 0) {
            return Response.status(Response.Status.OK)
                    .entity("The Message is empty - please insert message to be index.")
                    .build();
        }

        IndexRequest indexRequest = getIndexRequest(documentMessage, userAgent);
        StringBuilder response_message = new StringBuilder();

        try {
            IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
            if (indexResponse.getResult() == DocWriteResponse.Result.CREATED) {
                response_message.append("Document has been created successfully.");
            } else if (indexResponse.getResult() == DocWriteResponse.Result.UPDATED) {
                response_message.append("Document has been updated successfully.");
            } else {
                response_message.append("Error at indexing the Document.");
            }
        } catch (IOException e) {
            response_message.append("Error at indexing the Document.");
            e.printStackTrace();
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                return Response
                        .status(Response.Status.OK)
                        .entity(response_message.toString())
                        .build();
            }
        }
    }

}

