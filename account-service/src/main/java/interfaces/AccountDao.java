package interfaces;

import entities.Account;

import java.util.List;

public interface AccountDao {
    void save(Account account);
    Account getAccountByIdNumber(int id);
    Account getAccountByToken(String token);
    List<Account> getAccountByName(String name);
}
