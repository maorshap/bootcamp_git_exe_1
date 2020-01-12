package daos;

import entities.Account;
import interfaces.AccountDao;
import mybatis.mappers.AccountMapper;

import javax.inject.Inject;
import javax.inject.Singleton;

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

    //TODO::Change the return type to Optional<Account>.
    public Account getAccountByToken(String token){
        Account account = accountMapper.getAccountByToken(token);
        return account;
    }

}
