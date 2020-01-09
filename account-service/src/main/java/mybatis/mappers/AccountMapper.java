package mybatis.mappers;

import entities.Account;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface AccountMapper {

    Account getAccountById(int id);

    Account getAccountByToken(String token);

    List<Account> getAccountByName(String name);

    void insertAccount(Account accountEntity);
}
