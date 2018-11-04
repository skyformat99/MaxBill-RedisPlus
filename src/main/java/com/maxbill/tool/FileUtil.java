package com.maxbill.tool;

import java.io.*;

public class FileUtil {


    public static boolean writeStringToFile(String filePath, String data) {
        PrintStream printStream = null;
        try {
            File file = new File(filePath);
            printStream = new PrintStream(new FileOutputStream(file));
            printStream.println(data);
            printStream.close();
            return true;
        } catch (FileNotFoundException e) {
            return false;
        }
    }


    public static boolean readFileToString(String filePath) {
        try {
            StringBuffer sb = new StringBuffer("");
            FileReader reader = new FileReader(filePath);
            BufferedReader br = new BufferedReader(reader);
            String str = null;
            while ((str = br.readLine()) != null) {
                sb.append(str + "\r\n");
                System.out.println(str);
            }
            br.close();
            reader.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
