package com.maxbill.core.desktop;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class TopsMenu extends ContextMenu {

    private static TopsMenu topsMenu = null;

    /**
     * 构造函数
     */
    private TopsMenu() {
        MenuItem menuItem01 = new MenuItem();
        menuItem01.setText("  设置");
        menuItem01.setGraphic(new ImageView(new Image("/image/tops-menu-image.png")));
        MenuItem menuItem02 = new MenuItem();
        menuItem02.setText("  帮助");
        menuItem02.setGraphic(new ImageView(new Image("/image/tops-menu-image.png")));
        MenuItem menuItem03 = new MenuItem();
        menuItem03.setText("  社区");
        menuItem03.setGraphic(new ImageView(new Image("/image/tops-menu-image.png")));
        MenuItem menuItem04 = new MenuItem();
        menuItem04.setText("  建议");
        menuItem04.setGraphic(new ImageView(new Image("/image/tops-menu-image.png")));
        MenuItem menuItem05 = new MenuItem();
        menuItem05.setText("  关于");
        menuItem05.setGraphic(new ImageView(new Image("/image/tops-menu-image.png")));
        getItems().add(menuItem01);
        getItems().add(menuItem02);
        getItems().add(menuItem03);
        getItems().add(menuItem04);
        getItems().add(menuItem05);
        menuItem01.setOnAction(event -> {
        });
        menuItem02.setOnAction(event -> {
        });
        menuItem03.setOnAction(event -> {
        });
        menuItem04.setOnAction(event -> {
        });
        menuItem05.setOnAction(event -> {
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
