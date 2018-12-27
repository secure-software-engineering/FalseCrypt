using System;
using System.IO;
using System.Security.Cryptography;
using System.Text;

namespace FalseCrypt.Crypto
{
    public static class WeakSymmetricEncryption
    {
        internal static byte[] Encrypt(byte[] secretMessage, byte[] key, byte[] saltPayload)
        {
            if (key == null || key.Length != WeakCryptoConfig.KeySizeBytes)
                throw new ArgumentException($"Key needs to be {WeakCryptoConfig.KeySizeBytes} bytes!", nameof(key));

            if (secretMessage == null || secretMessage.Length < 1)
                throw new ArgumentException("Secret Message Required!", nameof(secretMessage));

            var payload = saltPayload ?? new byte[]{};

            var des = new DESCryptoServiceProvider
            {
                KeySize = WeakCryptoConfig.KeySizeBytes * 8,
                BlockSize = WeakCryptoConfig.BlockSizeBytes * 8,
                Mode = CipherMode.ECB,
                Padding = PaddingMode.PKCS7
            };
            des.IV = WeakCryptoConfig.IV;
            des.Key = key;

            byte[] cipherText;
            var encrypter = des.CreateEncryptor(key, WeakCryptoConfig.IV);
            using (var cipherStream = new MemoryStream())
            {
                var cryptoStream = new CryptoStream(cipherStream, encrypter, CryptoStreamMode.Write);
                using (var binaryWriter = new BinaryWriter(cryptoStream))
                    binaryWriter.Write(secretMessage);
                cipherText = cipherStream.ToArray();
            }

            using (var encryptedStream = new MemoryStream())
            {
                using (var binaryWriter = new BinaryWriter(encryptedStream))
                {
                    binaryWriter.Write(payload);
                    binaryWriter.Write(des.IV);
                    binaryWriter.Write(cipherText);
                }
                return encryptedStream.ToArray();
            }
        }

        internal static byte[] Decrypt(byte[] encryptedMessage, byte[] cryptKey, int saltLength)
        {
            //Basic Usage Error Checks
            if (cryptKey == null || cryptKey.Length != WeakCryptoConfig.KeySizeBytes)
                throw new ArgumentException($"CryptKey needs to be {WeakCryptoConfig.KeySizeBytes} bytes!",
                    nameof(cryptKey));

            if (encryptedMessage == null || encryptedMessage.Length == 0)
                throw new ArgumentException("Encrypted Message Required!", nameof(encryptedMessage));

            var ivLength = WeakCryptoConfig.BlockSizeBytes;

            var des = new DESCryptoServiceProvider
            {
                KeySize = WeakCryptoConfig.KeySizeBytes * 8,
                BlockSize = WeakCryptoConfig.BlockSizeBytes * 8,
                Mode = CipherMode.ECB, Padding = PaddingMode.PKCS7
            };
            var iv = new byte[ivLength];
            Array.Copy(encryptedMessage, saltLength, iv, 0, iv.Length);

            var decrypter = des.CreateDecryptor(cryptKey, iv);

            using (var plainTextStream = new MemoryStream())
            {
                var decrypterStream = new CryptoStream(plainTextStream, decrypter, CryptoStreamMode.Write);
                using (var binaryWriter = new BinaryWriter(decrypterStream))
                {
                    binaryWriter.Write(
                        encryptedMessage,
                        saltLength + iv.Length,
                        encryptedMessage.Length - saltLength - iv.Length
                    );
                }

                var pt = plainTextStream.ToArray();
                return pt;
            }
        }
    }
}
