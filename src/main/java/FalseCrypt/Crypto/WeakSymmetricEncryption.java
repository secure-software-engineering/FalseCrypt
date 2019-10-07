package FalseCrypt.Crypto;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

// XXX internal dropped? c# internal = java default?
public class WeakSymmetricEncryption {
	static byte[] Encrypt(byte[] secretMessage, byte[] key, byte[] saltPayload) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IOException, IllegalBlockSizeException, BadPaddingException
    {
		final ByteArrayOutputStream ret = new ByteArrayOutputStream();
		
        if (key == null || key.length != WeakCryptoConfig.KeySizeBytes)
            throw new IllegalArgumentException("Key needs to be {WeakCryptoConfig.KeySizeBytes} bytes!");

        if (secretMessage == null || secretMessage.length < 1)
            throw new IllegalArgumentException("Secret Message Required!");

        byte[] payload = saltPayload == null ? new byte[] {} : saltPayload;

        // Bug 12: Weak Encryption Provider // TODO in java you let java choose the provider, maybe better "weak algorithm?"
        // Bug 13: Weak Operation Mode
        // Bug 14: Not disposing IDisposable
        Cipher des = Cipher.getInstance("DES/ECB/PKCS5Padding"); // is identical to PKCS7 if 8 Bytes used, PKCS7 not supported by java
        
	     // Bug 15: Constant IV // TODO duplicated? 15 / 16
	     // Bug 17: Not disposing IDisposable
        // TODO cannot use IV
//        des.init(Cipher.ENCRYPT_MODE, 
//        		new SecretKeySpec(key, "DES"), 
//        		new IvParameterSpec(WeakCryptoConfig.IV));
        des.init(Cipher.ENCRYPT_MODE, 
        		new SecretKeySpec(key, "DES"));

        byte[] cipherText;
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        // Bug 18: Not disposing IDisposable / AutoClosable
    	final CipherOutputStream cos = new CipherOutputStream(os, des);
    	cos.write(secretMessage, 0, secretMessage.length);
    	cipherText = os.toByteArray();
    	// BUG 19: array with clear text content should be destroyed after a encryption. Comment below shows how.
    	// Arrays.fill(byte, 0); // TODO which array? cipherText -> after written

    	ret.write(payload);
    	ret.write(WeakCryptoConfig.IV);
    	ret.write(cipherText);
    	ret.write(des.doFinal());
    	
    	return ret.toByteArray();
    }

	static byte[] Decrypt(byte[] encryptedMessage, byte[] key, int saltLength) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException
    {
		final ByteArrayOutputStream ret = new ByteArrayOutputStream();
        //Basic Usage Error Checks
        if (key == null || key.length != WeakCryptoConfig.KeySizeBytes)
            throw new IllegalArgumentException("CryptKey needs to be {WeakCryptoConfig.KeySizeBytes} bytes!");

        if (encryptedMessage == null || encryptedMessage.length == 0)
            throw new IllegalArgumentException("Encrypted Message Required!");

        int ivLength = WeakCryptoConfig.BlockSizeBytes;

        // Bug 20: Weak Encryption Provider
        // Bug 21: Weak Operation Mode
        Cipher des = Cipher.getInstance("DES/ECB/PKCS5Padding");
        // TODO cannot use IV
//        des.init(Cipher.DECRYPT_MODE, 
//        		new SecretKeySpec(key, "DES"), 
//        		new IvParameterSpec(WeakCryptoConfig.IV));
        des.init(Cipher.DECRYPT_MODE, 
        		new SecretKeySpec(key, "DES"));
        
        byte[] iv = new byte[ivLength];
        System.arraycopy(encryptedMessage, saltLength, iv, 0, iv.length);
        
        byte[] strippedMessage = new byte[encryptedMessage.length - (saltLength + iv.length)];
        System.arraycopy(encryptedMessage, saltLength + iv.length, strippedMessage, 0, strippedMessage.length);
        
        final ByteArrayInputStream is = new ByteArrayInputStream(strippedMessage);
//        final ByteArrayInputStream is = new ByteArrayInputStream(
//        		encryptedMessage, 
//        		saltLength + iv.length, 
//        		encryptedMessage.length - (saltLength + iv.length));
        // TODO not AutoClosed
        final CipherInputStream cis = new CipherInputStream(is, des);
        
        
        byte[] buff = new byte[16];
        int bytesRead = 0;
        try {
			while ((bytesRead = cis.read(buff)) != -1) {
				ret.write(buff, 0, bytesRead);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        return ret.toByteArray();
    }
}
