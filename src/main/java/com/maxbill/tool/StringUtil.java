package com.maxbill.tool;

import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    /**
     * 拆分key:value形式字符返回value
     */
    public static String getValueString(String tempStr) {
        if (!StringUtils.isEmpty(tempStr) && appearStringNumber(tempStr, ":") == 1) {
            String[] tempStrArray = tempStr.split(":");
            return tempStrArray[1];
        } else {
            return "";
        }
    }

    /**
     * 获取指定字符串出现的次数
     */
    public static int appearStringNumber(String srcText, String findText) {
        int count = 0;
        Pattern p = Pattern.compile(findText);
        Matcher m = p.matcher(srcText);
        while (m.find()) {
            count++;
        }
        return count;
    }

}
