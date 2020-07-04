
 
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.spec.SecretKeySpec;
import javax.crypto.*;
 
/**
 * A utility class that encrypts or decrypts a file.
 * @author www.codejava.net
 *
 */
public class AES {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES";
 
    public static void encrypt(String key, File inputFile, File outputFile)
            throws CryptoException {
        doCrypto(Cipher.ENCRYPT_MODE, key, inputFile, outputFile);
    }
 
    public static void decrypt(String key, File inputFile, File outputFile)
            throws CryptoException {
        doCrypto(Cipher.DECRYPT_MODE, key, inputFile, outputFile);
    }
    
    //Tao khoa voi chieu dai 128, 192 hoac 256 bit
    public static String keyGen(int keySize)
    		throws NoSuchAlgorithmException{
    	KeyGenerator kgen = KeyGenerator.getInstance("AES");
    	kgen.init(keySize);
    	SecretKey aesKey = kgen.generateKey();
    	String key = Base64.getEncoder().encodeToString(aesKey.getEncoded());
    	return key;
    }
 
    private static void doCrypto(int cipherMode, String key, File inputFile,
            File outputFile) throws CryptoException {
        try {
            Key secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(cipherMode, secretKey);
             
            FileInputStream inputStream = new FileInputStream(inputFile);
            byte[] inputBytes;
            //Them HMAC trong truong hop encrypt
            if (cipherMode == Cipher.ENCRYPT_MODE) {
            	inputBytes = new byte[(int) inputFile.length() + 64];
                inputStream.read(inputBytes);
            	System.arraycopy(HMAC.hmacDigest(inputFile, key).getBytes(), 0, inputBytes, (int) inputFile.length(), 64);
            }
            else {
            	inputBytes = new byte[(int) inputFile.length()];
                inputStream.read(inputBytes);
            }
            
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