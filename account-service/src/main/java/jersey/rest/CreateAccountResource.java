package jersey.rest;

import boundaries.AccountName;
import entities.Account;
import interfaces.AccountDao;
import org.apache.commons.lang3.RandomStringUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.net.HttpURLConnection;
import java.util.List;

import static java.util.Objects.requireNonNull;

@Singleton
@Path("account-service/")
public class CreateAccountResource {
    private final static String ES_NAME_PREFIX = "logz";
    private final static int TOKEN_LENGTH = 32;
    private final AccountDao accountDao;

    @Inject
    public CreateAccountResource(AccountDao accountDao){
        this.accountDao = requireNonNull(accountDao);
    }

    @POST
    @Path("create-account")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createAccount(AccountName accountNameBoundary) {
        Account account = buildAccountEntity(accountNameBoundary.getAccountName());

        accountDao.save(account);

        List<Account> accounts = accountDao.getAccountByName(account.getName());

        return Response.status(HttpURLConnection.HTTP_OK)
                .entity(accounts.get(accounts.size() - 1))
                .build();
    }

    private Account buildAccountEntity(String accountName) {
        StringBuilder esIndexNameBuilder = new StringBuilder();
        esIndexNameBuilder.append(ES_NAME_PREFIX).append("-").append(RandomStringUtils.random(TOKEN_LENGTH, true, false));

        String token = RandomStringUtils.random(TOKEN_LENGTH, true, false);

        Account account = new Account(accountName, token, esIndexNameBuilder.toString());

        return account;
    }


}
