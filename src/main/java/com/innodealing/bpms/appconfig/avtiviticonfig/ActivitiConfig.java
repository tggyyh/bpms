package com.innodealing.bpms.appconfig.avtiviticonfig;

import com.alibaba.druid.Constants;
import com.alibaba.druid.pool.DruidDataSource;
import com.innodealing.bpms.unit.Generate;
import org.activiti.engine.impl.cfg.IdGenerator;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.List;

@Configuration
public class ActivitiConfig {

    @Autowired
    @Qualifier("activitiTransactionManager")
    private PlatformTransactionManager transactionManager;

    @Autowired
    @Qualifier("activitiDataSource")
    private DataSource dataSource;

    @Value("${process.bpmn.survive}")
    private String processBpmnSurvive;
    @Autowired
    private StringRedisTemplate redisTemplate;
    private final static String ID_GENERATOR="activiti_id_generator";
    @Bean
    public SpringProcessEngineConfiguration getProcessEngineConfiguration(){
        SpringProcessEngineConfiguration config =
                new SpringProcessEngineConfiguration();
        config.setDataSource(dataSource);
        config.setTransactionManager(transactionManager);
        config.setDatabaseType("mysql");
        config.setDatabaseSchemaUpdate("true");
        Resource[] processResources = new Resource[1] ;
        Resource underwritingQueueProcess1 = new ClassPathResource(processBpmnSurvive);
        processResources[0]=underwritingQueueProcess1;
        config.setDeploymentResources(processResources);
        config.setDeploymentName("innodealing");
        config.setIdGenerator(new IdGenerator() {
            @Override
            public String getNextId() {
                return String.valueOf(redisTemplate.opsForValue().increment(ID_GENERATOR,1));
            }
        });
        return config;
    }
}

