namespace FalseCrypt.Crypto
{
    public static class WeakCryptoConfig
    {
        public const int IterationCount = 1000;
        public const int SaltSizeBytes = 8;
        public const int KeySizeBytes = 8;
        public const int BlockSizeBytes = 8;

        public const string password = "482c811da5d5b4bc6d497ffa98491e38";

        public static byte[] IV = new byte[] {1, 2, 3, 4, 5, 6, 7, 8};
    }
}