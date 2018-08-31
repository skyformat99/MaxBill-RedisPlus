package com.maxbill.tool;

import java.io.*;

public class ObjectUtil {

    /**
     * 序列化对象
     */
    public static byte[] serialize(Object value) {
        if (value == null) {
            throw new NullPointerException("Can't serialize null");
        }
        byte[] rv = null;
        ByteArrayOutputStream bos = null;
        ObjectOutputStream os = null;
        try {
            bos = new ByteArrayOutputStream();
            os = new ObjectOutputStream(bos);
            os.writeObject(value);
            os.close();
            bos.close();
            rv = bos.toByteArray();
        } catch (IOException e) {
            throw new IllegalArgumentException("Non-serializable object", e);
        } finally {

        }
        return rv;
    }

    /**
     * 反序列化对象
     */
    public static Object deserialize(byte[] in) {
        Object rv = null;
        ByteArrayInputStream bis = null;
        ObjectInputStream is = null;
        try {
            if (in != null) {
                bis = new ByteArrayInputStream(in);
                is = new ObjectInputStream(bis);
                rv = is.readObject();
                is.close();
                bis.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeStream(is, bis);
        }
        return rv;
    }


    /**
     * 关闭流
     */
    private static void closeStream(ObjectInputStream is, ByteArrayInputStream bis) {
        try {
            if (is != null) {
                is.close();
            }
            if (bis != null) {
                bis.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
