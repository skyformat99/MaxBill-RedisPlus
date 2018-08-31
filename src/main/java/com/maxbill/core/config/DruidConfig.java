package com.maxbill.core.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.sql.DataSource;
import java.io.InputStream;

@Configuration
public class DruidConfig {

    private String baseUrl = System.getProperty("user.home");

    @Bean
    public DataSource druidDataSource() {
        DruidDataSource datasource = new DruidDataSource();
        datasource.setTestWhileIdle(false);
        datasource.setTestOnBorrow(false);
        datasource.setTestOnReturn(false);
        datasource.setUrl("jdbc:derby:" + baseUrl + "/.redis_plus;create=true");
        datasource.setDriverClassName("org.apache.derby.jdbc.EmbeddedDriver");
        return datasource;
    }

}
