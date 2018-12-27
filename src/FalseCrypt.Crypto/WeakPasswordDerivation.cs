using System.Security.Cryptography;
using System.Text;

namespace FalseCrypt.Crypto
{
    public static class WeakPasswordDerivation
    {
        public static string StringToHash(string input)
        {
            var hashProvider = new MD5CryptoServiceProvider();
            var inputBytes = Encoding.UTF8.GetBytes(input);
            var hash = hashProvider.ComputeHash(inputBytes);
            var sb = new StringBuilder();
            foreach (var t in hash)
                sb.Append(t.ToString("X2"));
            return sb.ToString();
        }


        public static PasswordDeriveData DerivePassword(string password)
        {
            var salt = WeakKeyGenerator.GenerateSalt();
            var generator = new Rfc2898DeriveBytes(password, salt, WeakCryptoConfig.IterationCount);

            var cryptKey = generator.GetBytes(WeakCryptoConfig.KeySizeBytes);
            return new PasswordDeriveData(cryptKey, salt);
        }
    }
}
