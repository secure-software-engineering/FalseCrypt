package FalseCrypt.Crypto;

public class WeakCryptoConfig {
	public final static int IterationCount = 1000;
    public final static int SaltSizeBytes = 8;
    public final static int KeySizeBytes = 8;
    public final static int BlockSizeBytes = 8;

    // Bug 2: A password, even it's hash, should not be hardcoded so it's easy to get it via decompilation
    public final static String Password = "482c811da5d5b4bc6d497ffa98491e38";

    public static byte[] IV = new byte[] {1, 2, 3, 4, 5, 6, 7, 8};
}
