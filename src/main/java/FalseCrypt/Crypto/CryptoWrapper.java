package FalseCrypt.Crypto;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class CryptoWrapper {
	
	private static Path getPathFromFile(final File file) {
		// TODO URI conversion may sometimes bad -> look wizard project
        return Paths.get(file.getAbsoluteFile().toURI());
	}
	
	private static byte[] getBytesFromPath(final Path path) throws IOException {
		return Files.readAllBytes(path);
	}
	
	private static void writeBytesToPath(final Path path, byte[] bytes) throws IOException {
		Files.write(path, bytes); // TODO maybe options needed FileMode.Create, FileAccess.Write, FileShare.ReadWrite
	}
	
	private static void renameFile(final File source) {
		File target = null;
		
		if (source == null || !source.exists()) {
			return;
		}
		
		if (source.getName().endsWith(".falsecrypt")) {
			target = new File(source.getAbsoluteFile() + ".tmp");
		} else if (source.getName().endsWith(".falsecrypt.tmp")) {
			target = new File((String) source.getAbsoluteFile().toString().subSequence(0, source.getAbsoluteFile().toString().length() - ".falsecrypt.tmp".length()));
		} else {
			target = new File(source.getAbsoluteFile() + ".falsecrypt");
		}
		if (target.exists()) {
			target.delete();
		}
		System.out.println(source.getAbsolutePath() + " -> " + target.getAbsolutePath());
		source.renameTo(target);
	}
	
	public static void EncryptFileWithPassword(File file, String password) throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException
    {
		if (file == null || !file.exists())
            throw new FileNotFoundException();
        
        Path target = getPathFromFile(file);
        byte[] bytes = getBytesFromPath(target);
        byte[] cipherText = EncryptMessage(bytes, password);
        writeBytesToPath(target, cipherText);
        renameFile(file);
    }

    public static void EncryptFile(File file, byte[] key, byte[] salt) throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException
    {
        if (file == null || !file.exists())
            throw new FileNotFoundException(file.getAbsolutePath());
        
        Path target = getPathFromFile(file);
        byte[] bytes = getBytesFromPath(target);
        byte[] cipherText = WeakSymmetricEncryption.Encrypt(bytes, key, salt);
        writeBytesToPath(target, cipherText);
        renameFile(file);
    }

    public static String EncryptMessage(String secretMessage, String password, Charset encoding) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException, IOException, IllegalBlockSizeException, BadPaddingException
    {
        if (secretMessage == null || secretMessage.isEmpty())
            throw new IllegalArgumentException("Secret Message Required!");
        
        PasswordDeriveData data = WeakPasswordDerivation.DerivePassword("password"); // TODO String Literal instead variable correct?
        return EncryptMessage(secretMessage, data.Key, data.Salt, encoding);
    }

    public static String EncryptMessage(String secretMessage, byte[] key, Charset encoding) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IOException, IllegalBlockSizeException, BadPaddingException
    {
        return EncryptMessage(secretMessage, key, null, encoding);
    }

    public static String EncryptMessage(String secretMessage, byte[] key, byte[] nonSecretPayload, Charset encoding) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IOException, IllegalBlockSizeException, BadPaddingException
    {
        if (secretMessage == null || secretMessage.isEmpty())
        	throw new IllegalArgumentException("Secret Message Required!");

        byte[] cipherText = EncryptMessage(secretMessage.getBytes(encoding), key, nonSecretPayload);
        return Base64.getEncoder().encodeToString(cipherText);
    }

    public static byte[] EncryptMessage(byte[] secretMessage, byte[] key) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IOException, IllegalBlockSizeException, BadPaddingException
    {
        return EncryptMessage(secretMessage, key, null);
    }

    public static byte[] EncryptMessage(byte[] secretMessage, byte[] key, byte[] nonSecretPayload) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IOException, IllegalBlockSizeException, BadPaddingException
    {
        return WeakSymmetricEncryption.Encrypt(secretMessage, key, nonSecretPayload);
    }

    public static byte[] EncryptMessage(byte[] secretMessage, String password) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException, IOException, IllegalBlockSizeException, BadPaddingException
    {
    	PasswordDeriveData data = WeakPasswordDerivation.DerivePassword(password);
        return WeakSymmetricEncryption.Encrypt(secretMessage, data.Key, data.Salt);
    }

    public static void DecryptFileWithPassword(File file, String password) throws IOException, InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException
    {
        if (file == null || !file.exists())
            throw new FileNotFoundException();

        Path target = getPathFromFile(file);
        byte[] bytes = getBytesFromPath(target);
        
        File tmp = new File(file.getAbsolutePath() + ".tmp");
        if (tmp.exists()) {
        	tmp.delete();
        }
        try (FileOutputStream fos = new FileOutputStream(tmp);) {
        	fos.write(DecryptMessage(bytes, password));
        }
        file.delete();
        renameFile(tmp);
    }

    public static void DecryptFile(File file, byte[] key) throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException
    {
        if (file == null || !file.exists())
            throw new FileNotFoundException();
        
        Path target = getPathFromFile(file);
        byte[] bytes = getBytesFromPath(target);

        File tmp = new File(file.getAbsoluteFile() + ".tmp");
        try (FileOutputStream fos = new FileOutputStream(tmp)) {
        	byte[] m = WeakSymmetricEncryption.Decrypt(bytes, key, WeakCryptoConfig.SaltSizeBytes);
        	fos.write(m);
        }
        file.delete();
        renameFile(file);
    }

    public static String DecryptMessage(String encryptedMessage, String password, Charset encoding) throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException, IOException
    {
        if (encryptedMessage == null || encryptedMessage.isEmpty())
            throw new IllegalArgumentException("Encrypted Message Required!");

        byte[] cipherText = Base64.getDecoder().decode(encryptedMessage);
        byte[] plainText = DecryptMessage(cipherText, password);
        return plainText == null ? null : new String(plainText, encoding);
    }

    public static String DecryptMessage(String encryptedMessage, byte[] key, Charset encoding) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IOException
    {
        if (encryptedMessage == null || encryptedMessage.isEmpty())
            throw new IllegalArgumentException("Encrypted Message Required!");
        
        byte[] cipherText = Base64.getDecoder().decode(encryptedMessage);
        byte[] plainText = WeakSymmetricEncryption.Decrypt(cipherText, key, 0);
        return plainText == null ? null : new String(plainText, encoding);
    }

    public static byte[] DecryptMessage(byte[] encryptedMessage, String password) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException, IOException
    {
        if (encryptedMessage == null || encryptedMessage.length == 0)
            throw new IllegalArgumentException("Encrypted Message Required!");
        
        byte[] salt = new byte[WeakCryptoConfig.SaltSizeBytes];
        System.arraycopy(encryptedMessage, 0, salt, 0, salt.length);
        PasswordDeriveData data = WeakPasswordDerivation.DerivePassword(password, salt);
        return WeakSymmetricEncryption.Decrypt(encryptedMessage, data.Key, data.Salt.length);
    }
}
