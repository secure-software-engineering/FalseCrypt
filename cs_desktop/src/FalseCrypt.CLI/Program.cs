// See https://aka.ms/new-console-template for more information

using System;
using System.IO;
using FalseCrypt.Crypto;


Console.Write("Enter Password to use the App: ");

var pwd = Console.ReadLine();

var hash = WeakPasswordDerivation.StringToHash(pwd);
if (hash == null || !hash.Equals(WeakCryptoConfig.Password, StringComparison.InvariantCultureIgnoreCase))
{
    Console.WriteLine("Password was not correct. Aborting!");
    return;
}
Console.WriteLine("Password correct.");
Console.WriteLine();
Console.WriteLine();

Console.WriteLine("What would you like to do?");
Console.WriteLine("\t1. Encrypt File");
Console.WriteLine("\t2. Encrypt Folder");
Console.WriteLine("\t3. Decrypt File");
Console.WriteLine("\t4. Decrypt Folder");
Console.WriteLine("\t0. Quit");


while (true)
{
    Console.Write("Chose your option: ");
    if (!int.TryParse(Console.ReadLine(), out var selectedOption))
    {
        continue;
    }
    switch (selectedOption)
    {
        case 0:
            return;
        case 1:
            EncryptFile();
            break;
        case 2:
            EncryptFolder();
            break;
        case 3:
            DecryptFile();
            break;
        case 4:
            DecryptFolder();
            break;
    }
} 

void EncryptFile()
{

    var file = Path.GetFullPath(WhileStringNotNull(() => GetFile("encrypt")));

    if (!File.Exists(file))
    {
        Console.WriteLine($"File '{file}' does not exist!");
        return;
    }

    var password = GetPassword();

    if (!YesNo($"Are you sure you want to encrypt file: '{file}'?"))
        return;

    EncryptionCryptoWrapper.EncryptFileWithPassword(new FileInfo(file), password);

    Console.WriteLine("Successfully encrypted.");
}

void EncryptFolder()
{
    
    var folder = Path.GetFullPath(WhileStringNotNull(() =>GetFolder("encrypt")));

    if (!Directory.Exists(folder))
    {
        Console.WriteLine($"Directory '{folder}' does not exist!");
        return;
    }

    var password = GetPassword();

    string[] files;
    try
    {
        files = Directory.GetFiles(folder, "*.*", SearchOption.AllDirectories);
    }
    catch (Exception e)
    {
        Console.WriteLine("Error: " + e.Message);
        return;
    }
    
    // BUG 1: Key derivation should not be performed outside a foreach block that is using its return value.
    // Otherwise, all operations in this loop have the same encryption key
    var keyData = WeakPasswordDerivation.DerivePassword(password);


    if (!YesNo($"Are you sure you want to encrypt folder: '{folder}'?"))
        return;
    
    foreach (var file in files)
    {
        if (!File.Exists(file))
            continue;
        EncryptionCryptoWrapper.EncryptFile(new FileInfo(file), keyData.Key, keyData.Salt);
    }

    Console.WriteLine("Successfully encrypted.");
}

void DecryptFile()
{
    
    var file = Path.GetFullPath(WhileStringNotNull(() => GetFile("encrypt")));

    if (!File.Exists(file))
    {
        Console.WriteLine($"File '{file}' does not exist!");
        return;
    }

    if (Path.GetExtension(file) != ".falsecrypt")
    {
        Console.WriteLine($"File '{file}' must have extension: .falsecrypt");
        return;
    }

    var password = GetPassword();

    try
    {
        EncryptionCryptoWrapper.DecryptFileWithPassword(new FileInfo(file), password);
    }
    catch (Exception)
    {
        Console.WriteLine("Wrong password.");
        return;
    }
    Console.WriteLine("Successfully decrypted");
}

void DecryptFolder()
{

    var folder = Path.GetFullPath(WhileStringNotNull(() => GetFolder("encrypt")));

    if (!Directory.Exists(folder))
    {
        Console.WriteLine($"Directory '{folder}' does not exist!");
        return;
    }

    var password = GetPassword();


    var files = Directory.GetFiles(folder, "*.falsecrypt", SearchOption.AllDirectories);


    // NOT A BUG for itself: The weakness of using the same key foreach file was caused by the encryption.
    // The decryption methods just matches contract the encryption sets
    var keyData = WeakPasswordDerivation.DerivePassword(password);

    foreach (var file in files)
    {
        if (!File.Exists(file))
            continue;
        try
        {
            EncryptionCryptoWrapper.DecryptFile(new FileInfo(file), keyData.Key);
        }
        catch (Exception e)
        {
            Console.WriteLine("Wrong password.");
            return;
        }
    }
    Console.WriteLine("Successfully decrypted");
}

string GetPassword()
{
    return WhileStringNotNull(ShowPasswordEnter);
}


string? GetFolder(string action)
{
    Console.Write("Enter the folder to " + action + ": ");
    return Console.ReadLine();
}

string? GetFile(string action)
{
    Console.Write("Enter the file to " + action + ": ");
    return Console.ReadLine();
}

string? ShowPasswordEnter()
{
    Console.Write("Enter the password: ");
    return Console.ReadLine();
}

string WhileStringNotNull(Func<string?> stringAction)
{
    string? value = null;
    while (value is null)
    {
        try
        {
            value = stringAction();
        }
        catch (Exception)
        {
            value = null;
        }
    }

    return value;
}

bool YesNo(string message)
{
    bool? result = null;
    do
    {
        Console.WriteLine(message + " - [Y]es;[N]o");
        var resultString = Console.ReadLine()?.ToUpperInvariant();
        if (resultString == "Y" || resultString == "YES")
            result = true;
        if (resultString == "N" || resultString == "NO")
            result = false;

    } while (!result.HasValue);

    return result.Value;
}
