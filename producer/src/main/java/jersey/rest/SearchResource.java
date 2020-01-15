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
import javax.ws.rs.HeaderParam;
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
import java.util.Optional;

import static java.util.Objects.requireNonNull;

@Singleton
@Path("bootcamp")
public class SearchResource {

    private static final String ACCOUNT_SERVICE_URL = "http://account_service:8090/account-service";

    private final RestHighLevelClient elasticsearchClient;
    private final AccountServiceClient accountServiceClient;


    @Inject
    public SearchResource(RestHighLevelClient elasticsearchClient) {
        this.elasticsearchClient = requireNonNull(elasticsearchClient);
        this.accountServiceClient = new AccountServiceClient(ACCOUNT_SERVICE_URL);
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
    public Response searchDocument(@Context UriInfo uriInfo, @HeaderParam("X-ACCOUNT-TOKEN") String token) {
        SearchRequest searchRequest;
        try {
            searchRequest = buildSearchRequest(uriInfo, token);
        }
        catch (NoSuchAccountException e) {
            return  RestUtils.buildResponse(HttpURLConnection.HTTP_UNAUTHORIZED, e.getMessage());
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

        try {
            SearchResponse searchResponse = elasticsearchClient.search(request, RequestOptions.DEFAULT);

            SearchHits searchHits = searchResponse.getHits();
            for (SearchHit hit : searchHits)
                sb.append(hit.getSourceAsMap()).append("\n");
        }
        catch (ElasticsearchStatusException e) {
            return RestUtils.buildResponse(HttpURLConnection.HTTP_NO_CONTENT, "The Account messages queue is empty from messages");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return RestUtils.buildResponse(HttpURLConnection.HTTP_OK, sb.toString());

    }

    private Account getAccountFromDB(String token) throws NoSuchAccountException {
        Optional<Account> optionalAccount = accountServiceClient.getAccountFromDB(token);
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
