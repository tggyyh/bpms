package com.innodealing.bpms.appconfig.wsdlconfig;

import com.innodealing.bpms.domain.WSSynService;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WSConfig {
    @Value(value = "${wsdl.server.path}")
    private String  path;
    @Bean
    public WSSynService wsSynService() {
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setAddress(path);
        factory.setServiceClass(WSSynService.class);
        factory.getInInterceptors().add(new LoggingInInterceptor());
        factory.getOutInterceptors().add(new LoggingOutInterceptor());
        WSSynService synService = (WSSynService) factory.create();
        return synService;
    }

//    public void setPath(String path) {
//        this.path = path;
//    }
//
//    public String getPath() {
//        return path;
//    }
}
