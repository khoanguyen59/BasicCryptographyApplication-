import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class RSA{
	public static KeyPair keyGen(int keySize) throws IOException {
        KeyPair kp = null;
        try {
            SecureRandom sr = new SecureRandom();
            KeyPairGenerator pair;
            pair = KeyPairGenerator.getInstance("RSA");
            pair.initialize(keySize, sr);
            kp = pair.genKeyPair();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //get Public Key and Private Key then save them into files for later use
        byte[] publicByte = kp.getPublic().getEncoded();
        byte[] privateByte = kp.getPrivate().getEncoded();
        System.out.print(GUI.URL_DIR);
        File pubFile = new File(GUI.URL_DIR + "/PublicKey/");
        if (!pubFile.exists()){
            pubFile.mkdir();
            // If you require it to make the entire directory path including parents,
            // use directory.mkdirs(); here instead.
        }
        FileOutputStream fosPub = new FileOutputStream(GUI.URL_DIR + "/PublicKey/" + new SimpleDateFormat("dd-MM-yyyy_hhmmss_" + Integer.toString(keySize)).format(new Date()));
        fosPub.write(publicByte);
        fosPub.close();
        File pvtFile = new File(GUI.URL_DIR + "/PrivateKey/");
        if (!pvtFile.exists()){
            pvtFile.mkdir();
            // If you require it to make the entire directory path including parents,
            // use directory.mkdirs(); here instead.
        }
        FileOutputStream fosPvt = new FileOutputStream("PrivateKey/" + new SimpleDateFormat("dd-MM-yyyy_hhmmss_" + Integer.toString(keySize)).format(new Date()));
        fosPvt.write(privateByte);
        fosPvt.close();
        return kp;
    }
	
	 public static PublicKey loadPublicKey(String path) 
			 throws Exception {
		 PublicKey pk = null;
		 try{
			 FileInputStream fis = new FileInputStream(path);
	         byte[] keyBytes = new byte[fis.available()];
	         fis.read(keyBytes);
	         fis.close();
	         X509EncodedKeySpec spec =new X509EncodedKeySpec(keyBytes);
	         KeyFactory kf = KeyFactory.getInstance("RSA");
	         pk=kf.generatePublic(spec);
		 } catch (Exception e) {
	            e.printStackTrace();
		 }
		 return pk;
	 }
	 
	 public static PrivateKey loadPrivateKey(String path) throws Exception {
	        PrivateKey pk = null;        
	        try{
	            FileInputStream fis = new FileInputStream(path);
	            byte[] keyBytes = new byte[fis.available()];
	            fis.read(keyBytes);
	            fis.close();
	            PKCS8EncodedKeySpec  spec = new PKCS8EncodedKeySpec(keyBytes);
	            KeyFactory kf = KeyFactory.getInstance("RSA");
	            pk=kf.generatePrivate(spec);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return pk;
	    }
	
	public static void encrypt(String publicKeyStr, File inputFile, File outputFile) 
			throws CryptoException, InvalidKeySpecException {
		try {
			//Application inputs always be String so we have to convert them to Key
			byte[] publicBytes = Base64.getDecoder().decode(publicKeyStr);
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			PublicKey publicKey = keyFactory.generatePublic(keySpec);

			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			
			FileInputStream inputStream = new FileInputStream(inputFile);
            byte[] inputBytes = new byte[(int) inputFile.length() + 64];
            inputStream.read(inputBytes);
            //Them HMAC trong truong hop encrypt
           	System.arraycopy(HMAC.hmacDigest(inputFile, publicKeyStr).getBytes(), 0, inputBytes, (int) inputFile.length(), 64);
           	
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
	 
	public static void decrypt(String privateKeyStr, File inputFile, File outputFile)
	        throws CryptoException, InvalidKeySpecException {
		try {
			//Application inputs always be String so we have to convert them to Key
			byte[] privateBytes = Base64.getDecoder().decode(privateKeyStr);
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateBytes);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
			
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			
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