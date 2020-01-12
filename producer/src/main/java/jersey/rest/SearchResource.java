package jersey.rest;

import javax.inject.Inject;
import javax.inject.Singleton;

import Exceptions.NoSuchAccountException;
import clients.AccountServiceClient;
import entities.Account;
import org.elasticsearch.ElasticsearchStatusException;
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
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

@Singleton
@Path("bootcamp")
public class SearchResource {

    private final RestHighLevelClient elasticsearchClient;

    @Inject
    public SearchResource(RestHighLevelClient elasticsearchClient) {
        this.elasticsearchClient = requireNonNull(elasticsearchClient);
    }


    /**
     * "/search" entry point.
     *
     * @param uriInfo TCP context
     * @return Response invoke by Search
     */
    @GET
    @Path("search/{token}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchDocument(@Context UriInfo uriInfo, @PathParam("token") String token) {
        SearchRequest searchRequest;
        try {
            searchRequest = buildSearchRequest(uriInfo, token);
        }
        catch (NoSuchAccountException e) {
            return Response.status(HttpURLConnection.HTTP_UNAUTHORIZED)
                    .entity(e.getMessage())
                    .build();
        }

        return buildSearchResponse(searchRequest);
    }

    private SearchRequest buildSearchRequest(UriInfo uriInfo, String token) throws NoSuchAccountException {
        BoolQueryBuilder boolQueryBuilder = createBoolQueryBuilder(uriInfo);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(boolQueryBuilder);

        Account account = getAccountFromDB(token);
        String indexName = account.getEsIndexName().toLowerCase();

        SearchRequest searchRequest = new SearchRequest(indexName);
        searchRequest.source(searchSourceBuilder);

        return searchRequest;
    }

    private Response buildSearchResponse(SearchRequest request) {
        StringBuilder sb = new StringBuilder();
        int responseStatus = HttpURLConnection.HTTP_INTERNAL_ERROR;

        try {
            SearchResponse searchResponse = elasticsearchClient.search(request, RequestOptions.DEFAULT);
            responseStatus = HttpURLConnection.HTTP_OK;

            SearchHits searchHits = searchResponse.getHits();
            for (SearchHit hit : searchHits)
                sb.append(hit.getSourceAsMap()).append("\n");
        }
        catch (ElasticsearchStatusException e) {
            responseStatus = HttpURLConnection.HTTP_NO_CONTENT;
            sb.append("The Account is empty from messages");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            return Response.status(responseStatus)
                    .entity(sb.toString())
                    .build();
        }

    }

    private Account getAccountFromDB(String token) throws NoSuchAccountException {
        Optional<Account> optionalAccount = AccountServiceClient.getAccountFromDB(token);
        if (!optionalAccount.isPresent()) {
            throw new NoSuchAccountException("There is no such account with the given token in the database");
        }
        return optionalAccount.get();
    }


    private BoolQueryBuilder createBoolQueryBuilder(UriInfo uriInfo) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
        for (Map.Entry<String, List<String>> param : queryParams.entrySet()) {
            QueryBuilder curQuery = QueryBuilders.matchQuery(param.getKey(), param.getValue().iterator().next());
            boolQueryBuilder.must(curQuery);
        }

        return boolQueryBuilder;
    }
}
