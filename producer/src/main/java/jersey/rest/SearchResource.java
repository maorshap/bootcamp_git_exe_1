package jersey.rest;

import javax.inject.Inject;
import javax.inject.Singleton;
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

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;

@Singleton
@Path("bootcamp")
public class SearchResource {

    private final static String INDEX_NAME = "messages";
    private final RestHighLevelClient elasticsearchClient;

    @Inject
    public SearchResource(RestHighLevelClient elasticsearchClient){
        this.elasticsearchClient = requireNonNull(elasticsearchClient);
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
    public Response searchDocument(@Context UriInfo uriInfo) {
        SearchRequest searchRequest = buildSearchRequest(uriInfo);
        return buildResponse(searchRequest);
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
        catch(Exception e){
            e.printStackTrace();
        }
        finally {
            return Response.status(responseStatus)
                    .entity(sb.toString())
                    .build();
        }

    }
}
