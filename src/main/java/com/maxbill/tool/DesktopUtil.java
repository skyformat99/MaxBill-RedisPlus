package com.maxbill.tool;

import java.awt.*;
import java.net.URI;

public class DesktopUtil {

    public static void openBrowse(String url) {
        try {
            // 创建一个URI实例
            URI uri = URI.create(url);
            // 获取当前系统桌面扩展
            Desktop dp = java.awt.Desktop.getDesktop();
            // 判断系统桌面是否支持要执行的功能
            if (dp.isSupported(java.awt.Desktop.Action.BROWSE)) {
                // 获取系统默认浏览器打开链接
                dp.browse(uri);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
