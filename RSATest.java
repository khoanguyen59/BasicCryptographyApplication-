
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;

import javax.crypto.KeyGenerator;
import javax.crypto.*;

public class RSATest {
    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
    	KeyPair key = RSA.keyGen(4096);
    	String publicKey = Base64.getEncoder().encodeToString(key.getPublic().getEncoded());
    	String privateKey = Base64.getEncoder().encodeToString(key.getPrivate().getEncoded());
        File inputFile = new File("document.txt");
        File encryptedFile = new File("documentRSA.encrypted");
        File decryptedFile = new File("documentRSA.decrypted");
         
        try {
            RSA.encrypt(publicKey, inputFile, encryptedFile);
            RSA.decrypt(privateKey, encryptedFile, decryptedFile);
        } catch (CryptoException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }
}
