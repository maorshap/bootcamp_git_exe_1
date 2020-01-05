package jersey.rest;

import boundaries.AccountName;
import daos.AccountDao;
import entities.Account;
import org.apache.commons.lang3.RandomStringUtils;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.HttpURLConnection;
import java.util.List;

@Singleton
@Path("account-service/")
public class RestResources {
    private final static String ES_NAME_PREFIX = "logz";
    private final static int TOKEN_LENGTH = 32;

    @POST
    @Path("create-account")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createAccount(AccountName accountNameBoundary) {
        Account account = buildAccountEntity(accountNameBoundary);

        AccountDao.getInstance().save(account);

        List<Account> accounts = AccountDao.getInstance().getAccountByName(accountNameBoundary.getAccountName());

        return Response.status(HttpURLConnection.HTTP_OK)
                .entity(accounts.get(accounts.size() - 1))
                .build();
    }

    @GET
    @Path("account/token/{token}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAccountByToken(@PathParam("token") String token) {
        Account account = AccountDao.getInstance().getAccountByToken(token);
        if (account == null) {
            return Response.status(HttpURLConnection.HTTP_UNAUTHORIZED)
                    .entity("There is no such account with the given token in the system.")
                    .build();
        }
        return Response.status(HttpURLConnection.HTTP_OK)
                .entity(account)
                .build();

    }

    private Account buildAccountEntity(AccountName accountNameBoundary) {
        Account account = new Account();

        StringBuilder esIndexNameBuilder = new StringBuilder();
        esIndexNameBuilder.append(ES_NAME_PREFIX).append("-").append(RandomStringUtils.random(TOKEN_LENGTH, true, false));

        account.setName(accountNameBoundary.getAccountName());
        account.setToken(RandomStringUtils.random(TOKEN_LENGTH, true, false));
        account.setEsIndexName(esIndexNameBuilder.toString());

        return account;
    }
}
