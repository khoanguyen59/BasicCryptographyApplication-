import java.io.File;
import java.math.BigInteger;
import java.util.Base64;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.KeyGenerator;
import javax.crypto.*;

public class DESTest {
	public static String toHex(String arg) {
	    return String.format("%040x", new BigInteger(1, arg.getBytes()));
	}
	
    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException {
    	String key = DES.keyGen(56);
        File inputFile = new File("ChuongI.doc");
        File encryptedFile = new File("ChuongI_DES.encrypted");
        File decryptedFile = new File("ChuongI_DES.decrypted");
        System.out.print(key.getBytes());
        System.out.print("\n");
        System.out.print(toHex(key));
        System.out.print("\n");
        System.out.print(toHex(key).length());
       
        try {
            DES.encrypt(key, inputFile, encryptedFile);
            DES.decrypt(key, encryptedFile, decryptedFile);
        } catch (CryptoException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }
}