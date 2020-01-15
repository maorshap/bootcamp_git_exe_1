package guice.modules;

import com.google.inject.name.Names;
import mybatis.daos.AccountDao;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.mybatis.guice.MyBatisModule;
import org.mybatis.guice.datasource.builtin.PooledDataSourceProvider;
import org.mybatis.guice.datasource.helper.JdbcHelper;
import utils.JsonParser;

import java.util.Properties;

public class MyBatisAccountModule extends MyBatisModule {
    private final Properties myBatisProperties;
    private final static String MY_BATIS_CONFIG_FILE = "mybatis/mybatis.config";

    public MyBatisAccountModule(){
        this.myBatisProperties = JsonParser.fromJsonFile(MY_BATIS_CONFIG_FILE, Properties.class);
    }

    @Override
    protected void initialize() {
        bind(DefaultObjectWrapperFactory.class);
        bind(DefaultObjectFactory.class);

        install(JdbcHelper.MySQL);
        bindTransactionFactoryType(JdbcTransactionFactory.class);
        bindDataSourceProviderType(PooledDataSourceProvider.class);
        Names.bindProperties(this.binder(), myBatisProperties);

        addMapperClass(AccountDao.class);
    }
}
