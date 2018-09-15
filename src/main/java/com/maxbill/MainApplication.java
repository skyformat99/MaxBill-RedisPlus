package com.maxbill;

import com.maxbill.core.desktop.DesktopApp;
import javafx.application.Platform;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;

@ServletComponentScan
@SpringBootApplication
@MapperScan("com.maxbill.base.dao")
public class MainApplication extends DesktopApp {

    public static ConfigurableApplicationContext context;

    public static void main(String[] args) {
        //启动后台服务
        context = SpringApplication.run(MainApplication.class, args);
        //启动桌面服务
        launch(args);
    }

}
