package mybatis.daos;

import entities.Account;

import java.util.List;

public interface AccountDao {

    Account getAccountById(int id);

    Account getAccountByToken(String token);

    List<Account> getAccountByName(String name);

    void insertAccount(Account accountEntity);
}
