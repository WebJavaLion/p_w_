package ru.pw.telegram.java.configuration;

import com.zaxxer.hikari.HikariDataSource;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Properties;

/**
 * @author Lev_S
 */

@Configuration
@EnableTransactionManagement
public class DbConfiguration {

    @Value("${host}")
    private String host;

    @Value("${user}")
    private String user;

    @Value("${password}")
    private String password;

    @Value("${databaseName}")
    private String databaseName;

    @Value("${portNumber}")
    private String portNumber;

    @Bean
    public DataSourceConnectionProvider connectionProvider() {
        return new DataSourceConnectionProvider(new TransactionAwareDataSourceProxy(dataSource()));
    }

    @Bean
    public DSLContext dslContext() {
        return DSL.using(connectionProvider(), SQLDialect.POSTGRES_10);
    }

    @Bean(destroyMethod = "close")
    public HikariDataSource dataSource() {
        final HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setMaximumPoolSize(100);
        hikariDataSource.setMinimumIdle(10);
        hikariDataSource.setMaxLifetime(10000);
        hikariDataSource.setDataSourceClassName("org.postgresql.ds.PGSimpleDataSource");

        Properties properties = new Properties();
        properties.put("user", user);
        properties.put("password", password);
        properties.put("databaseName", databaseName);
        properties.put("serverName", host);
        properties.put("portNumber", portNumber);
        hikariDataSource.setDataSourceProperties(properties);

        return hikariDataSource;
    }

    @Bean
    public org.jooq.Configuration configuration() {
        return new DefaultConfiguration().set(connectionProvider()).set(SQLDialect.POSTGRES_10);
    }
}
