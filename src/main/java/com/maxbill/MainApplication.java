package com.maxbill;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MainApplication extends Application {

    final String serverUrl = "http://127.0.0.1:8080/root";

    public static void main(String[] args) {
        //启动后台服务
        SpringApplication.run(MainApplication.class, args);
        //启动桌面服务
        launch(args);
    }

    public void start(Stage stage) {
        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();
        webEngine.load(serverUrl);
        stage.setScene(new Scene(webView, 1300, 800));
        stage.setTitle("Redis Studio");
        stage.getIcons().add(new Image("/static/image/task-logo.png"));
        stage.show();
    }
}
