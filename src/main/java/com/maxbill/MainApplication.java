package com.maxbill;

import com.maxbill.core.desktop.Desktop;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;


@SpringBootApplication
public class MainApplication extends Desktop {


    public static ConfigurableApplicationContext context;

    public static void main(String[] args) {
        launch(args);
    }

}
