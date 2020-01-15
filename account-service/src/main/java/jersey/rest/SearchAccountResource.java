package jersey.rest;

import entities.Account;
import mybatis.daos.AccountDao;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.net.HttpURLConnection;

import static java.util.Objects.requireNonNull;

@Singleton
@Path("account-service/")
public class SearchAccountResource {
    private final AccountDao accountDao;

    @Inject
    public SearchAccountResource(AccountDao accountDao){
        this.accountDao = requireNonNull(accountDao);
    }

    @GET
    @Path("account/token/{token}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAccountByToken(@PathParam("token") String token) {
        Account account = accountDao.getAccountByToken(token);
        if (account == null) {
            return Response.status(HttpURLConnection.HTTP_NOT_FOUND)
                    .entity("There is no such account with the given token in the database.")
                    .build();
        }
        return Response.status(HttpURLConnection.HTTP_OK)
                .entity(account)
                .build();

    }
}
