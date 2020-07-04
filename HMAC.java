

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class HMAC {
    
    public static String hmacDigest(File msg, String keyString) throws IOException {
        String digest = null;
        try {
            SecretKeySpec key = new SecretKeySpec(keyString.getBytes("UTF-8"), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(key);
            FileInputStream fis = new FileInputStream(msg);
            byte[] keyBytes = new byte[fis.available()];
            fis.read(keyBytes);
            fis.close();
            byte[] bytes = mac.doFinal(keyBytes);
            
            StringBuffer hash = new StringBuffer();
            for (int i = 0; i < bytes.length; i++) {
                String hex = Integer.toHexString(0xFF & bytes[i]);
                if (hex.length() == 1) {
                    hash.append('0');
                }
                hash.append(hex);
            }
            digest = hash.toString();
        } catch (UnsupportedEncodingException e) {
        } catch (InvalidKeyException e) {
        } catch (NoSuchAlgorithmException e) {
        }
        return digest;
    }
    
    public static String hmacDigestDecrypt(File msg, String keyString) throws IOException {
        String digest = null;
        try {
            SecretKeySpec key = new SecretKeySpec(keyString.getBytes("UTF-8"), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(key);
            FileInputStream fis = new FileInputStream(msg);
            byte[] keyBytes = new byte[fis.available() - 64];
            fis.read(keyBytes);
            fis.close();
            byte[] bytes = mac.doFinal(keyBytes);
            
            StringBuffer hash = new StringBuffer();
            for (int i = 0; i < bytes.length; i++) {
                String hex = Integer.toHexString(0xFF & bytes[i]);
                if (hex.length() == 1) {
                    hash.append('0');
                }
                hash.append(hex);
            }
            digest = hash.toString();
        } catch (UnsupportedEncodingException e) {
        } catch (InvalidKeyException e) {
        } catch (NoSuchAlgorithmException e) {
        }
        return digest;
    }
    
    public static String hmacDigestStr(String msg, String keyString) {
        String digest = null;
        try {
            SecretKeySpec key = new SecretKeySpec(keyString.getBytes("UTF-8"), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(key);
            
            byte[] bytes = mac.doFinal(msg.getBytes("UTF-8"));
            
            StringBuffer hash = new StringBuffer();
            for (int i = 0; i < bytes.length; i++) {
                String hex = Integer.toHexString(0xFF & bytes[i]);
                if (hex.length() == 1) {
                    hash.append('0');
                }
                hash.append(hex);
            }
            digest = hash.toString();
        } catch (UnsupportedEncodingException e) {
        } catch (InvalidKeyException e) {
        } catch (NoSuchAlgorithmException e) {
        }
        return digest;
    }
}