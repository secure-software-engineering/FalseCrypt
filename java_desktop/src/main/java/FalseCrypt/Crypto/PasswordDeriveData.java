package FalseCrypt.Crypto;

public class PasswordDeriveData {
	public final byte[] Key;
	public final byte[] Salt;
	
	public PasswordDeriveData(final byte[] Key, final byte[] Salt) {
		this.Key = Key;
		this.Salt = Salt;
	}
}
