package FalseCrypt.Crypto;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import FalseCrypt.Crypto.CryptoWrapper;
import FalseCrypt.Crypto.PasswordDeriveData;
import FalseCrypt.Crypto.WeakCryptoConfig;
import FalseCrypt.Crypto.WeakKeyGenerator;
import FalseCrypt.Crypto.WeakPasswordDerivation;

class CryptoTest {
	private static final byte[] salt = new byte[] {-26, 102, 102, -28, -72, 93, 90, -117};
	private static final byte[] key = new byte[] {8, 41, 99, -80, -51, -115, 68, 103};
	
	@Test
	public void SaltAlwaysTheSameStart() {
		byte[] testArray = WeakKeyGenerator.GenerateSalt();
		Assert.assertArrayEquals(testArray, salt);
	}
	
	@Test
	public void PasswordKeyOnceTheSame() throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        PasswordDeriveData data = WeakPasswordDerivation.DerivePassword("Password");
        Assert.assertEquals(WeakCryptoConfig.SaltSizeBytes, data.Salt.length);
        Assert.assertEquals(WeakCryptoConfig.KeySizeBytes, data.Key.length);
        Assert.assertArrayEquals(salt, data.Salt);
        Assert.assertArrayEquals(key, data.Key);
    }

	@Test
	public void PasswordKeyAlwaysTheSame() throws NoSuchAlgorithmException, InvalidKeySpecException {
		for (int i = 0; i < 5; i++) {
			PasswordDeriveData data = WeakPasswordDerivation.DerivePassword("Password");
			Assert.assertEquals(WeakCryptoConfig.SaltSizeBytes, data.Salt.length);
			Assert.assertEquals(WeakCryptoConfig.KeySizeBytes, data.Key.length);
			Assert.assertArrayEquals(salt, data.Salt);
	        Assert.assertArrayEquals(key, data.Key);
		}
	}
	
//	@Test
	public void EncryptEncryptEqual() throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, IOException
    {
        final String message = "This is a secret Message";
        final String password = "password";

        String cipherText1 =
            CryptoWrapper.EncryptMessage(message, password, Charset.forName("UTF-8"));
        
        PasswordDeriveData data = WeakPasswordDerivation.DerivePassword(password);
        PasswordDeriveData data2 = WeakPasswordDerivation.DerivePassword(password);
        Assert.assertArrayEquals(data.Key, data2.Key);
        Assert.assertArrayEquals(data.Salt, data2.Salt);
        
        String cipherText2 = CryptoWrapper.EncryptMessage(message, data.Key, Charset.forName("UTF-8"));
        
//        System.out.println(CryptoWrapper.DecryptMessage(cipherText1, password, Charset.forName("UTF-8")));
//        System.out.println(CryptoWrapper.DecryptMessage(cipherText2, password, Charset.forName("UTF-8")));
        
        // strip IV / salt, not always present
        cipherText1 = cipherText1.substring(WeakCryptoConfig.SaltSizeBytes + WeakCryptoConfig.IV.length);
        cipherText2 = cipherText2.substring(WeakCryptoConfig.IV.length);
        Assert.assertEquals(cipherText1.length(), cipherText2.length());
        Assert.assertEquals(cipherText1, cipherText2);
    }
	
	@Test
	public void EncryptDecrypt() throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, IOException {
		String message = "This is a secret Message";
		String cipherText = CryptoWrapper.EncryptMessage(message, "password", Charset.forName("UTF-8"));
		String decryptedMessage = CryptoWrapper.DecryptMessage(cipherText, "password", Charset.forName("UTF-8"));
		Assert.assertEquals(message, decryptedMessage);
	}

	@Test
	public void EncryptDecryptManual() throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, IOException {
		String message = "This is a secret Message";
		PasswordDeriveData data = WeakPasswordDerivation.DerivePassword("password");
		String cipherText = CryptoWrapper.EncryptMessage(message, data.Key, Charset.forName("UTF-8"));
		String decryptedMessage = CryptoWrapper.DecryptMessage(cipherText, data.Key, Charset.forName("UTF-8"));
		Assert.assertEquals(message, decryptedMessage);
	}
}
