package com.oldbaby.oblib.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 对外提供MD5方法
 */
public class MD5 {

    private static MD5Al md5 = new MD5Al();

    public static String getMD5(String val) {

        return md5.getMD5ofStr(val);
    }

    public static String getMD5(byte[] val) {

        return md5.getMD5OfBytes(val);
    }

    public static String getDecMD5(String val) {

        return md5.getMD5ofDecStr(val);
    }

    public static String getMD5Original(String val)
            throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(val.getBytes());
        byte[] m = md5.digest(); // 加密
        return getHexString(m);
    }

    public static String getDecMD5Original(String val)
            throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(val.getBytes());
        byte[] m = md5.digest();// 加密
        return getDecString(m);
    }

    private static String getHexString(byte[] b) {
        StringBuffer hexValue = new StringBuffer();

        for (int i = 0; i < b.length; i++) {
            int val = ((int) b[i]) & 0xff;
            if (val < 16)
                hexValue.append("0");
            hexValue.append(Integer.toHexString(val));
        }

        return hexValue.toString();

    }

    private static String getDecString(byte[] b) {
        StringBuffer value = new StringBuffer();

        for (int i = 0; i < b.length; i++) {
            int val = ((int) b[i]) & 0xff;
            if (val < 100 && val >= 10)
                value.append("0");
            else if (val < 10)
                value.append("00");
            value.append(val);
        }

        return value.toString();

    }
}
