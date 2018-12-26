using System.Security.Cryptography;

namespace FalseCrypt.Crypto
{
    public static class WeakKeyGenerator
    {
        public static byte[] GenerateRandomBytes(int size)
        {
            //TODO: use size
            return new byte[] {1, 2, 3, 4, 5, 6, 7, 8};
        }
    }

    public static class WeakPasswordDerivation
    {
        public static PasswordDeriveData DerivePassword(string password)
        {
            var salt = WeakKeyGenerator.GenerateRandomBytes(WeakCryptoConfig.SaltSizeBytes);
            var generator = new Rfc2898DeriveBytes(password, WeakCryptoConfig.SaltSizeBytes, WeakCryptoConfig.IterationCount);

            //TODO: Weaken key length
            var cryptKey = generator.GetBytes(128 / 8);
            return new PasswordDeriveData(cryptKey, salt);
        }
    } 

    public struct PasswordDeriveData
    {
        public byte[] Key { get; }

        public byte[] Salt { get; }

        public PasswordDeriveData(byte[] key, byte[] salt)
        {
            Key = key;
            Salt = salt;
        }
    }

    public static class WeakCryptoConfig
    {
        public static int IterationCount = 1000;
        public static int SaltSizeBytes = 8;
    }
}
