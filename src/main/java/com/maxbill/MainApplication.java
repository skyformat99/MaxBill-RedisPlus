package com.maxbill;

import com.maxbill.core.desktop.Desktop;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;


@SpringBootApplication
@MapperScan("com.maxbill.base.dao")
public class MainApplication extends Desktop {


    public static ConfigurableApplicationContext context;

    public static void main(String[] args) {
        context = SpringApplication.run(MainApplication.class);
        launch(args);
    }

}
