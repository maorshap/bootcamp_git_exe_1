package interfaces;

import entities.Account;

//TODO::Change the return type of getAccountByToken to Optional<Account>.
public interface AccountDao {
    void save(Account account);
    Account getAccountByToken(String token);
}
