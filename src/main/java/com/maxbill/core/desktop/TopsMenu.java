package com.maxbill.core.desktop;

import com.maxbill.tool.DesktopUtil;
import com.maxbill.tool.ItemUtil;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URI;

public class TopsMenu extends ContextMenu {

    private static TopsMenu topsMenu = null;

    /**
     * 构造函数
     */
    private TopsMenu() {
        MenuItem menuItem01 = new MenuItem();
        menuItem01.setText("  设置");
        menuItem01.setGraphic(new ImageView(new Image("/image/top-menu01.png")));
        MenuItem menuItem02 = new MenuItem();
        menuItem02.setText("  反馈");
        menuItem02.setGraphic(new ImageView(new Image("/image/top-menu02.png")));
        MenuItem menuItem03 = new MenuItem();
        menuItem03.setText("  帮助");
        menuItem03.setGraphic(new ImageView(new Image("/image/top-menu03.png")));
        MenuItem menuItem04 = new MenuItem();
        menuItem04.setText("  版本");
        menuItem04.setGraphic(new ImageView(new Image("/image/top-menu04.png")));
        MenuItem menuItem05 = new MenuItem();
        menuItem05.setText("  关于");
        menuItem05.setGraphic(new ImageView(new Image("/image/top-menu05.png")));
        MenuItem menuItem06 = new MenuItem();
        menuItem06.setText("  退出");
        menuItem06.setGraphic(new ImageView(new Image("/image/top-menu06.png")));
        getItems().add(menuItem01);
        getItems().add(menuItem02);
        getItems().add(menuItem03);
        getItems().add(menuItem04);
        getItems().add(menuItem05);
        getItems().add(menuItem06);
        menuItem01.setOnAction(event -> {
            new AlertBox().display("设置");
        });
        menuItem02.setOnAction(event -> {
            new AlertBox().display("反馈");
        });
        menuItem03.setOnAction(event -> {
            new AlertBox().display("帮助");
        });
        menuItem04.setOnAction(event -> {
            new AlertBox().display("版本");
        });
        menuItem05.setOnAction(event -> {
            new AlertBox().display("关于");
        });
        menuItem06.setOnAction(event -> {
            System.exit(0);
        });
    }

    /**
     * 获取实例
     */
    public static TopsMenu getInstance() {
        if (topsMenu == null) {
            topsMenu = new TopsMenu();
        }
        StringBuffer styleBuffer = new StringBuffer();
        styleBuffer.append("-fx-min-width: 138;");
        styleBuffer.append("-fx-border-width: 1;");
        styleBuffer.append("-fx-border-radius: 5;");
        styleBuffer.append("-fx-border-color:silver;");
        styleBuffer.append("-fx-background-color: white;");
        topsMenu.setStyle(styleBuffer.toString());
        return topsMenu;
    }


}
