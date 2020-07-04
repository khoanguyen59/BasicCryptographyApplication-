import java.io.File;
import java.util.Base64;
import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;
import javax.crypto.*;

public class AESTest {
    public static void main(String[] args) throws NoSuchAlgorithmException {
    	String key = AES.keyGen(128);
        File inputFile = new File("assignment1.pdf");
        File encryptedFile = new File("document.encrypted");
        File decryptedFile = new File("document.decrypted");
         
        try {
            AES.encrypt(key, inputFile, encryptedFile);
            AES.decrypt(key, encryptedFile, decryptedFile);
        } catch (CryptoException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }
}