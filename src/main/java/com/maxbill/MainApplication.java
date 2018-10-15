package com.maxbill;

import com.maxbill.core.desktop.DesktopApp;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.web.servlet.ServletComponentScan;

@ServletComponentScan
@MapperScan("com.maxbill.base.dao")
public class MainApplication extends DesktopApp {

    public static void main(String[] args) {
        //启动桌面服务
        launch(args);
    }

}
