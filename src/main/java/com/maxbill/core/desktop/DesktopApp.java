package com.maxbill.core.desktop;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class DesktopApp extends Application {

    private final String appName = "Redis Studio";
    private final String appPath = "http://127.0.0.1:9999/root";
    private final String appIcon = "/static/image/task-logo.png";


    public void start(Stage stage) {
        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();
        webEngine.load(appPath);
        stage.setScene(new Scene(webView, 1300, 800));
        stage.setTitle(appName);
        stage.getIcons().add(new Image(appIcon));
        stage.setResizable(false);
        stage.show();
    }

}
