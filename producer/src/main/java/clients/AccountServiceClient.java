package clients;

import entities.Account;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import java.util.Optional;

public class AccountServiceClient {

    private static final String ACCOUNT_SERVICE_URL = "http://account_service:8090/account-service";
    private static final String GET_ACCOUNT_TARGET = ACCOUNT_SERVICE_URL + "/account/token";

    private AccountServiceClient(){}

    /**
     * Retrieve Optional with Account from DB  - by token.
     * @param token
     * @return
     */
    public static Optional<Account> getAccountFromDB(String token){
        Client client = ClientBuilder.newClient();
        Account accountFromDb = client.target(GET_ACCOUNT_TARGET)
                .path(token)
                .request(MediaType.APPLICATION_JSON)
                .get(Account.class);

        return Optional.ofNullable(accountFromDb);
    }
}
