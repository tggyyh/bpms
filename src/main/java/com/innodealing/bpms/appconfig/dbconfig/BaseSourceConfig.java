package com.innodealing.bpms.appconfig.dbconfig;

import com.github.pagehelper.PageInterceptor;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;

import javax.sql.DataSource;
import java.util.Properties;

public class BaseSourceConfig {
    private static PageInterceptor interceptor;

    public static PageInterceptor getInterceptor() {
        if (interceptor == null) {
            interceptor = new PageInterceptor();
            Properties properties = new Properties();
            properties.setProperty("rowBoundsWithCount", "true");
            properties.setProperty("offsetAsPageNum", "true");
            properties.setProperty("pageSizeZero", "true");
            properties.setProperty("helperDialect", "mysql");
            properties.setProperty("reasonable", "true");
            properties.setProperty("supportMethodsArguments", "true");
            properties.setProperty("autoRuntimeDialect", "true");
            interceptor.setProperties(properties);
        }
        return interceptor;
    }

    public static SqlSessionFactory getSessionFactory(DataSource dataSource, String aliasPackages) throws Exception {
        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();

        sessionFactory.setDataSource(dataSource);

        //分页插件
        sessionFactory.setPlugins(new Interceptor[]{getInterceptor()});

        sessionFactory.setTypeAliasesPackage(aliasPackages);
        sessionFactory.setVfs(SpringBootVFS.class);
        SqlSessionFactory factory = sessionFactory.getObject();
        factory.getConfiguration().setMapUnderscoreToCamelCase(true);
        return factory;
    }

}