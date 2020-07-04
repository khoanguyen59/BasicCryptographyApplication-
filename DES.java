
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class DES {
	public static String keyGen(int keySize)
    		throws NoSuchAlgorithmException{
    	KeyGenerator kgen = KeyGenerator.getInstance("DES");
    	SecureRandom sr = new SecureRandom();
    	kgen.init(keySize, sr);
    	SecretKey DESKey = kgen.generateKey();
    	String key = Base64.getEncoder().encodeToString(DESKey.getEncoded());
    	return key;
    }
    
    public static SecretKey loadKey(String path)  {
        SecretKey secretKey = null;
        try{
            FileInputStream fis = new FileInputStream(path);
            byte[] keyBytes = new byte[fis.available()];
            fis.read(keyBytes);
            fis.close();
            secretKey = new SecretKeySpec(keyBytes,"DES");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return secretKey;
    }
    
    public static void encrypt(String key, File inputFile, File outputFile) throws CryptoException, InvalidKeySpecException {
        try{
        	DESKeySpec secretKey = new DESKeySpec(key.getBytes());
        	SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        	SecretKey DESKey = keyFactory.generateSecret(secretKey);
            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, DESKey);
            
            FileInputStream inputStream = new FileInputStream(inputFile);
            byte[] inputBytes = new byte[(int) inputFile.length() + 64];
            inputStream.read(inputBytes);
            
          //Them HMAC trong truong hop encrypt
        	System.arraycopy(HMAC.hmacDigest(inputFile, key).getBytes(), 0, inputBytes, (int) inputFile.length(), 64);
        	
            byte[] outputBytes = cipher.doFinal(inputBytes);
             
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            outputStream.write(outputBytes);
             
            inputStream.close();
            outputStream.close();
        } catch (NoSuchPaddingException | NoSuchAlgorithmException
                | InvalidKeyException | BadPaddingException
                | IllegalBlockSizeException | IOException ex) {
            throw new CryptoException("Error encrypting/decrypting file", ex);
        }
    }
    
    public static void decrypt(String key, File inputFile, File outputFile) throws CryptoException, InvalidKeySpecException {
    	try{
    		DESKeySpec secretKey = new DESKeySpec(key.getBytes());
        	SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        	SecretKey DESKey = keyFactory.generateSecret(secretKey);
            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, DESKey);
            
            FileInputStream inputStream = new FileInputStream(inputFile);
            byte[] inputBytes = new byte[(int) inputFile.length()];
            inputStream.read(inputBytes);
             
            byte[] outputBytes = cipher.doFinal(inputBytes);
             
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            outputStream.write(outputBytes);
             
            inputStream.close();
            outputStream.close();
        } catch (NoSuchPaddingException | NoSuchAlgorithmException
                | InvalidKeyException | BadPaddingException
                | IllegalBlockSizeException | IOException ex) {
            throw new CryptoException("Error encrypting/decrypting file", ex);
        }
    }
}