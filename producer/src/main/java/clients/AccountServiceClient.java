package clients;

import entities.Account;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import java.util.Optional;

public class AccountServiceClient {

    private String serviceUrl;
    private static final String GET_ACCOUNT_PATH = "/account/token";
    private static Logger LOGGER = LogManager.getLogger(AccountServiceClient.class);

    public AccountServiceClient(String serviceUrl){
        this.serviceUrl = serviceUrl;
    }


    /**
     * Retrieve Optional with Account from DB  - by token.
     * @param token
     * @return
     */
    public Optional<Account> getAccountFromDB(String token)  {
        Client client = ClientBuilder.newClient();
        Account accountFromDb = null;
        try {
            accountFromDb = client.target(serviceUrl + GET_ACCOUNT_PATH)
                    .path(token)
                    .request(MediaType.APPLICATION_JSON)
                    .get(Account.class);
        }
        catch(NotFoundException e){
           LOGGER.error("There is no such account with the given token in the database");
        }

        return Optional.ofNullable(accountFromDb);
    }
}
