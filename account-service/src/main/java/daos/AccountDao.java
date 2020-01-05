package daos;

import entities.Account;
import mybatis.mappers.AccountMapper;

import mybatis.util.MybatisUtil;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.List;

public class AccountDao {

    private static AccountDao accountDao = new AccountDao();
    private SqlSessionFactory sqlSessionFactory = null;

    private AccountDao(){
        sqlSessionFactory = MybatisUtil.getSqlSessionFactory();
    }

    public static AccountDao getInstance(){
        return accountDao;
    }

    public void save(Account account){
        SqlSession session = sqlSessionFactory.openSession();
        AccountMapper mapper = session.getMapper(AccountMapper.class);
        mapper.insertAccount(account);
        session.commit();
        session.close();
    }

    public Account getAccountByIdNumber(int id){
        SqlSession session = sqlSessionFactory.openSession();
        AccountMapper mapper = session.getMapper(AccountMapper.class);
        Account account = mapper.selectAccountById(id);
        session.commit();
        session.close();
        return account;
    }

    public Account getAccountByToken(String token){
        SqlSession session = sqlSessionFactory.openSession();
        AccountMapper mapper = session.getMapper(AccountMapper.class);
        Account account = mapper.selectAccountByToken(token);
        session.commit();
        session.close();
        return account;
    }

    public List<Account> getAccountByName(String name){
        SqlSession session = sqlSessionFactory.openSession();
        AccountMapper mapper = session.getMapper(AccountMapper.class);
        List<Account> accountList = mapper.selectAccountByName(name);
        session.commit();
        session.close();
        return accountList;
    }


}
