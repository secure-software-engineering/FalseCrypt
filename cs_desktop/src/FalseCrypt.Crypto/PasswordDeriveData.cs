namespace FalseCrypt.Crypto
{
    public ref struct PasswordDeriveData
    {
        public byte[] Key { get; }

        public byte[] Salt { get; }

        public PasswordDeriveData(byte[] key, byte[] salt)
        {
            Key = key;
            Salt = salt;
        }
    }
}