package daos;

import entities.Account;
import interfaces.AccountDao;
import mybatis.mappers.AccountMapper;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

import static java.util.Objects.requireNonNull;

@Singleton
public class MySqlAccountDao implements AccountDao {

    private final AccountMapper accountMapper;

    @Inject
    public MySqlAccountDao(AccountMapper accountMapper){
        this.accountMapper = requireNonNull(accountMapper);
    }

    public void save(Account account){
        requireNonNull(account);
        accountMapper.insertAccount(account);
    }

    public Account getAccountByIdNumber(int id){
       Account account = accountMapper.getAccountById(id);
       return account;
    }

    public Account getAccountByToken(String token){
        Account account = accountMapper.getAccountByToken(token);
        return account;
    }

    public List<Account> getAccountByName(String name){
        List<Account> accountList = accountMapper.getAccountByName(name);
        return accountList;
    }

}
