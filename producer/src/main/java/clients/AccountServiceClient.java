package clients;

import entities.Account;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

public class AccountServiceClient {

    private AccountServiceClient(){}

    public static Account getAccountFromDB(String token){
        Client client = ClientBuilder.newClient();
        return client.target("http://account_service:8090/account-service/account/token")
                .path(token)
                .request(MediaType.APPLICATION_JSON)
                .get(Account.class);
    }
}
