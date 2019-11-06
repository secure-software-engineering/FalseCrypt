package FalseCrypt.Crypto;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class WeakPasswordDerivation {
	public static String StringToHash(String input) throws NoSuchAlgorithmException
    {
        // Bug 6: Using a weak hashing provider
        // Bug 7: Not disposing IDisposable
        if (input == null)
            return null;
        
        MessageDigest hashProvider = MessageDigest.getInstance("MD5");
        byte[] inputBytes = input.getBytes(Charset.forName("UTF-8"));
        byte[] hash = hashProvider.digest(inputBytes);
        return new BigInteger(1, hash).toString(16);
    }
	
	public static PasswordDeriveData DerivePassword(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
		return DerivePassword(password, null);
	}

	// https://stackoverflow.com/a/24418666/6598045
    public static PasswordDeriveData DerivePassword(String password, byte[] argSalt) throws NoSuchAlgorithmException, InvalidKeySpecException
    {
    	byte[] salt = argSalt == null ? WeakKeyGenerator.GenerateSalt() : argSalt;
    	// https://docs.oracle.com/javase/8/docs/technotes/guides/security/SunProviders.html
    	SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
    	// Bug 8: Iteration count is too low
        // Bug 9: Salt size is constant 8 bytes long and hence to short
        // Bug 10: Not disposing IDisposable
    	PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray(), salt, WeakCryptoConfig.IterationCount, WeakCryptoConfig.KeySizeBytes * 8);
    	SecretKey secretKey = factory.generateSecret(pbeKeySpec);
    	byte[] key = new byte[WeakCryptoConfig.KeySizeBytes];
    	System.arraycopy(secretKey.getEncoded(), 0, key, 0, WeakCryptoConfig.KeySizeBytes);
    	
    	// Bug 11: Password as a string allows memory dump attacks
    	String strKey = new String(key);

        return new PasswordDeriveData(key, salt);
    }
}
