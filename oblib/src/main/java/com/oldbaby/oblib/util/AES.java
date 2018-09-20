package com.oldbaby.oblib.util;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.regex.Pattern;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AES {

    private static Charset PLAIN_TEXT_ENCODING = Charset.forName("UTF-8");

    public static final String KEY_MOBILE = "c2ee391f74a720fb6afd16f5ed9911ab";
    public static final String KEY_EMAIL = "b89edcea1fee22a187f330c4f96b82cd";

    /**
     * 解密
     *
     * @param str
     * @param key
     * @return
     */
    public static String decrypt(String str, String key) {
        try {
            byte[] keyBytes = Arrays.copyOf(key.getBytes("ASCII"), 16);

            SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            byte[] cleartext = hexStringToByteArray(str);
            byte[] ciphertextBytes = cipher.doFinal(cleartext);

            return new String(ciphertextBytes, PLAIN_TEXT_ENCODING);
            // return new String(Hex.decodeHex()(ciphertextBytes));

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 十六进制字符串转byte数组
     *
     * @param hex
     * @return
     */
    private static byte[] hexStringToByteArray(String hex) {
        Pattern replace = Pattern.compile("^0x");
        String s = replace.matcher(hex).replaceAll("");

        byte[] b = new byte[s.length() / 2];
        for (int i = 0; i < b.length; i++) {
            int index = i * 2;
            int v = Integer.parseInt(s.substring(index, index + 2), 16);
            b[i] = (byte) v;
        }
        return b;
    }

}
