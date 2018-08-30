package com.maxbill.core.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DruidConfig {

    private String baseUrl = System.getProperty("user.dir");

    @Bean
    public DataSource druidDataSource() {
        DruidDataSource datasource = new DruidDataSource();
        datasource.setTestWhileIdle(false);
        datasource.setTestOnBorrow(false);
        datasource.setTestOnReturn(false);
        datasource.setUrl("jdbc:derby:" + baseUrl + "/RedisPlus;create=true");
        datasource.setDriverClassName("org.apache.derby.jdbc.EmbeddedDriver");
        return datasource;
    }

}
