package com.innodealing.bpms.appconfig.dbconfig;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = {"com.innodealing.bpms.mapper"}, sqlSessionFactoryRef = BpmsDateSourceConfig.SESSION_FACTORY_NAME)
public class BpmsDateSourceConfig extends BaseSourceConfig {
    static final String DATA_SOURCE_NAME = "bpmsDataSource";
    static final String DATA_SOURCE_PREFIX = "bpms.datasource";
    static final String TRANSACTION_NAME = "bpmsTransactionManager";
    static final String SESSION_FACTORY_NAME = "bpmsSqlSessionFactory";

    //innodealing数据源下需要设置mybatis别名的包数组
    static final String ALIAS_PACKAGES = "com.innodealing.bpms.model,com.innodealing.bpms.common.model";

    @Bean(name = DATA_SOURCE_NAME)
    @ConfigurationProperties(prefix = DATA_SOURCE_PREFIX)
    @Primary
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = TRANSACTION_NAME)
    @Primary
    public DataSourceTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }

    @Bean(name = SESSION_FACTORY_NAME)
    @Primary
    public SqlSessionFactory sqlSessionFactory(@Qualifier(DATA_SOURCE_NAME) DataSource dataSource) throws Exception {
        return super.getSessionFactory(dataSource, ALIAS_PACKAGES);

    }

}