package com.maxbill;

import com.maxbill.core.desktop.DesktopApp;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.maxbill.base.dao")
public class MainApplication extends DesktopApp {


    public static void main(String[] args) {
        //启动后台服务
        SpringApplication.run(MainApplication.class, args);
        //启动桌面服务
        launch(args);
    }


}
