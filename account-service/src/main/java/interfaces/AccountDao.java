package interfaces;

import entities.Account;

import java.util.List;

public interface AccountDao {
    void save(Account account);
    Account getAccountByToken(String token);
}
