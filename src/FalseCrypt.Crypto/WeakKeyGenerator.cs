using System;

namespace FalseCrypt.Crypto
{
    public static class WeakKeyGenerator
    {
        private const int SecretSeed = 852641973; 

        private static Random RandomGenerator => new Random(SecretSeed);

        public static byte[] GenerateSalt()
        {
            var byteArray = new byte[WeakCryptoConfig.SaltSizeBytes]; 
            RandomGenerator.NextBytes(byteArray);
            return byteArray;
        } 
    }
}
