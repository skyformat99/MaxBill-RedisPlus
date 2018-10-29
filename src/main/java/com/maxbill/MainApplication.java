package com.maxbill;

import com.maxbill.core.desktop.Desktop;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@MapperScan("com.maxbill.base.dao")
public class MainApplication extends Desktop {

    public static void main(String[] args) {
        launch(args);
    }

}
