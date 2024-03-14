using System.Linq;
using System.Text;
using FalseCrypt.Crypto;
using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace CryptoTest
{
    [TestClass]
    public class WeakCryptoTest
    {
        [TestMethod]
        public void SaltAlwaysTheSameStart()
        {
            var array = new byte[] {88, 144, 21, 224, 8, 102, 122, 218};
            var testArray = WeakKeyGenerator.GenerateSalt();
            Assert.AreEqual(true, testArray.SequenceEqual(array));
        }

        [TestMethod]
        public void PasswordKeyOnceTheSame()
        {
            var salt = new byte[] { 88, 144, 21, 224, 8, 102, 122, 218 };
            var key = new byte[] { 172, 19, 215, 234, 237, 198, 102, 232 };
            var data = WeakPasswordDerivation.DerivePassword("Password");

            Assert.AreEqual(WeakCryptoConfig.SaltSizeBytes, data.Salt.Length);
            Assert.AreEqual(WeakCryptoConfig.KeySizeBytes, data.Key.Length);
            Assert.AreEqual(true, data.Salt.SequenceEqual(salt));
            Assert.AreEqual(true, data.Key.SequenceEqual(key));
        }

        [TestMethod]
        public void PasswordKeyAlwaysTheSame()
        {
            var salt = new byte[] { 88, 144, 21, 224, 8, 102, 122, 218 };
            var key = new byte[] { 172, 19, 215, 234, 237, 198, 102, 232 };
            
            for (int i = 0; i < 5; i++)
            {
                var data = WeakPasswordDerivation.DerivePassword("Password");
                Assert.AreEqual(WeakCryptoConfig.SaltSizeBytes, data.Salt.Length);
                Assert.AreEqual(WeakCryptoConfig.KeySizeBytes, data.Key.Length);
                Assert.AreEqual(true, data.Salt.SequenceEqual(salt));
                Assert.AreEqual(true, data.Key.SequenceEqual(key));
            }
        }

        [TestMethod]
        public void EncryptEncryptEqual()
        {
            var message = "This is a secret Message";
            var password = "password";

            var cipherText1 =
                EncryptionCryptoWrapper.EncryptMessage(message, password, Encoding.UTF8);

            var data = WeakPasswordDerivation.DerivePassword(password);
            var cipherText2 = EncryptionCryptoWrapper.EncryptMessage(message, data.Key, Encoding.UTF8);

            Assert.AreNotEqual(cipherText1, cipherText2);
        }

        [TestMethod]
        public void EncryptDecrypt()
        {
            var message = "This is a secret Message";
            var cipherText =
                EncryptionCryptoWrapper.EncryptMessage(message, "password", Encoding.UTF8);
            var decryptedMessage = EncryptionCryptoWrapper.DecryptMessage(cipherText, "password", Encoding.UTF8);
            Assert.AreEqual(message, decryptedMessage);
        }

        [TestMethod]
        public void EncryptDecryptManual()
        {
            var message = "This is a secret Message";
            var data = WeakPasswordDerivation.DerivePassword("password");
            var cipherText = EncryptionCryptoWrapper.EncryptMessage(message, data.Key, Encoding.UTF8);
            var decryptedMessage = EncryptionCryptoWrapper.DecryptMessage(cipherText, data.Key, Encoding.UTF8);
            Assert.AreEqual(message, decryptedMessage);
        }
    }
}
