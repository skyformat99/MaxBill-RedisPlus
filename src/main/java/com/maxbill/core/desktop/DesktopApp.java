package com.maxbill.core.desktop;

import com.maxbill.MainApplication;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import org.springframework.context.ConfigurableApplicationContext;

public class DesktopApp extends Application {

    private double xOffset = 0;
    private double yOffset = 0;
    //private boolean isMax = false;
    private final String uiType = "1";
    private final Integer prefWidth = 1300;
    private final Integer prefHeight = 800;
    private final Integer titlePrefWidth = 30;
    private final Integer titlePrefHeight = 30;
    private final String appName = "RedisPlus";
    private final String appShow = "/static/styles/winui.css";
    private final String appFont = "/static/rcfont/redisplus.ttf";
    private final String appPath = "http://127.0.0.1:9999/root";
    private final String appIcon = "/static/image/task-logo.png";


    @Override
    public void start(Stage stage) throws Exception {
        if (uiType.equals("0")) {
            this.useDefaultStage(stage);
        } else {
            this.useMyselfStage(stage);
        }
    }

    /**
     * 默认标题栏实现
     */
    public void useDefaultStage(Stage stage) {
        stage.setTitle(appName);
        stage.setScene(new Scene(getWebView()));
        stage.getIcons().add(new Image(appIcon));
        stage.setResizable(false);
        stage.show();
    }


    /**
     * 自定义标题栏实现
     */
    public void useMyselfStage(Stage stage) throws Exception {
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(new Scene(getWinRoot(stage)));
        stage.getIcons().add(new Image(appIcon));
        stage.setResizable(false);
        stage.setTitle(appName);
        stage.setFullScreen(false);
        stage.setOnShowing((WindowEvent e) -> {
        });
        stage.setOnShown((WindowEvent e) -> {
        });
        stage.show();
    }

    /**
     * 自定义主窗体
     */
    private VBox getWinRoot(Stage stage) throws Exception {
        //主题窗口
        VBox winRoot = new VBox();
        winRoot.setId("winRoot");
        String style = this.getClass().getResource(appShow).toString();
        winRoot.getStylesheets().add(style);
        //内容窗口
        VBox content = new VBox();
        content.getChildren().addAll(getWebView());
        winRoot.getChildren().addAll(getTopView(stage), content);
        winRoot.setOnMousePressed((MouseEvent event) -> {
            event.consume();
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        winRoot.setOnMouseDragged((MouseEvent event) -> {
            event.consume();
            stage.setX(event.getScreenX() - xOffset);
            if (event.getScreenY() - yOffset < 0) {
                stage.setY(0);
            } else {
                stage.setY(event.getScreenY() - yOffset);
            }
        });
        return winRoot;
    }

    /**
     * 页面视图
     */
    private WebView getWebView() {
        WebView webView = new WebView();
        webView.setContextMenuEnabled(false);
        webView.setPrefSize(prefWidth, prefHeight);
        webView.setFontSmoothingType(FontSmoothingType.GRAY);
        WebEngine webEngine = webView.getEngine();
        webEngine.setJavaScriptEnabled(true);
        webEngine.setUserAgent("RedisPlus WebEngine");
        webEngine.load(appPath);
        return webView;
    }

    /**
     * 自定顶部窗体
     */
    private VBox getTopView(Stage stage) throws Exception {
        // 顶部视图
        VBox topView = new VBox();
        topView.setId("topView");
        // 标题栏视图
        AnchorPane titleView = new AnchorPane();
        // 标题图标
        Label winImage = new Label();
        winImage.setPrefSize(titlePrefWidth, titlePrefHeight);
        winImage.setId("winImage");
        titleView.getChildren().add(winImage);
        // 标题文字
        Label winTitle = new Label();
        winTitle.setPrefSize(100, titlePrefHeight);
        winTitle.setId("winTitle");
        winTitle.setFont(Font.loadFont(this.getClass().getResource(appFont).openStream(), 12));
        winTitle.setText(appName);
        winTitle.setAlignment(Pos.BASELINE_LEFT);
        titleView.getChildren().add(winTitle);
        // 最小化按钮
        Label winAbate = new Label();
        winAbate.setPrefSize(titlePrefWidth, titlePrefHeight);
        winAbate.setId("winAbate");
        winAbate.setText("一");
        winAbate.setFont(Font.loadFont(this.getClass().getResource(appFont).openStream(), 12));
        winAbate.setScaleX(0.6);
        winAbate.setScaleY(0.6);
        winAbate.setAlignment(Pos.BASELINE_CENTER);
        winAbate.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
                stage.setIconified(true);
            }
        });
        titleView.getChildren().add(winAbate);
        // 最大化按钮
        /*
        Label winRaise = new Label();
        winRaise.setPrefSize(titlePrefWidth, titlePrefHeight);
        winRaise.setId("winRaise");
        winRaise.setText("口");
        winRaise.setFont(Font.loadFont(this.getClass().getResource(appFont).openStream(), 12));
        winRaise.setScaleX(0.6);
        winRaise.setScaleY(0.6);
        winRaise.setAlignment(Pos.BASELINE_CENTER);
        winRaise.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
                Rectangle2D rectangle2d = Screen.getPrimary().getVisualBounds();
                isMax = !isMax;
                if (isMax) {
                    // 最大化
                    stage.setX(rectangle2d.getMinX());
                    stage.setY(rectangle2d.getMinY());
                    stage.setWidth(rectangle2d.getWidth());
                    stage.setHeight(rectangle2d.getHeight());
                } else {
                    // 缩放回原来的大小
                    stage.setX(xOffset);
                    stage.setY(xOffset);
                    stage.setWidth(prefWidth);
                    stage.setHeight(prefHeight);
                }
            }
        });
        titleView.getChildren().add(winRaise);
        */
        // 关闭按钮
        Label winClose = new Label();
        winClose.setPrefSize(titlePrefWidth, titlePrefHeight);
        winClose.setId("winClose");
        winClose.setText("X");
        winClose.setFont(Font.loadFont(this.getClass().getResource(appFont).openStream(), 12));
        winClose.setScaleX(0.6);
        winClose.setScaleY(0.6);
        winClose.setAlignment(Pos.BASELINE_CENTER);
        winClose.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
                this.shutdown();
                //Platform.exit();
                System.exit(0);
            }
        });
        titleView.getChildren().add(winClose);
        // 顶部布局
        //AnchorPane.setLeftAnchor(winImage, 5.0);
        //AnchorPane.setLeftAnchor(winTitle, 40.0);
        //AnchorPane.setRightAnchor(winAbate, 60.0);
        //AnchorPane.setRightAnchor(winRaise, 30.0);
        //AnchorPane.setRightAnchor(winClose, 5.0);
        AnchorPane.setLeftAnchor(winImage, 5.0);
        AnchorPane.setLeftAnchor(winTitle, 40.0);
        AnchorPane.setRightAnchor(winAbate, 30.0);
        AnchorPane.setRightAnchor(winClose, 5.0);
        topView.getChildren().add(titleView);
        return topView;
    }

    /**
     * 关闭内置服务容器
     */
    private void shutdown() {
        ConfigurableApplicationContext context = MainApplication.context;
        if (null != context) {
            //关闭容器服务
            context.close();
        }
    }

}
