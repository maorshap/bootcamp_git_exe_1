package mybatis.mappers;

import entities.Account;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface AccountMapper {

    @Results({
            @Result(property = "id", column = "account_number"),
            @Result(property = "name", column = "name"),
            @Result(property = "token", column = "token"),
            @Result(property = "esIndexName", column = "es_index_name")
    })
    @Select("SELECT * FROM Account WHERE id = #{id}")
    Account selectAccountById(int id);

    @Results({
            @Result(property = "id", column = "account_number"),
            @Result(property = "name", column = "name"),
            @Result(property = "token", column = "token"),
            @Result(property = "esIndexName", column = "es_index_name")
    })
    @Select("SELECT * FROM Account WHERE token = #{token}")
    Account selectAccountByToken(String token);

    @Results({
            @Result(property = "id", column = "account_number"),
            @Result(property = "name", column = "name"),
            @Result(property = "token", column = "token"),
            @Result(property = "esIndexName", column = "es_index_name")
    })
    @Select("SELECT * FROM Account WHERE name = #{name}")
    List<Account> selectAccountByName(String name);

    @Insert("INSERT into Account(name,token,es_index_name) VALUES(#{name}, #{token}, #{esIndexName})")
    void insertAccount(Account accountEntity);
}
