package FalseCrypt.Crypto;

import java.util.Random;

public class WeakKeyGenerator {
	// Bug 3: Constant seed makes it attackers easy to recreate the pseudo random sequence
    private final static int SecretSeed = 852641973; 

    
    public static byte[] GenerateSalt()
    {
    	// BUG 4: A property with object initializer means that with each access a new instance of random with the same seed will be created. 
    	Random RandomGenerator = new Random(SecretSeed);
        byte[] byteArray = new byte[WeakCryptoConfig.SaltSizeBytes]; 
        // BUG 5: System.Random is not cryptographically secure
        RandomGenerator.nextBytes(byteArray);
        return byteArray;
    }
}
