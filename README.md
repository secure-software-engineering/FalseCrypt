# FalseCrypt - How NOT to do encryption
FalseCrypt is demonstration project for Java, Android and .NET containing (by intent) misuses of the language's dedicates cryptographic APIs. 

# Purpose
FalseCrypt is a file encryption tool like TrueCrypt or your favourite ransomware. Its code contains many weaknesses like the insecure usage of the old DES block cipher. The project was developed to demonstrate findings of SAST Tools. 

# Disclaimer
By any means the source code of this project does NOT contain any good usage of the .NET Crypto API. It should therefore not be used for any serious production code.

Though the encryption is not secure, the tool is able to successfully perfom a encryption. The authors do not take ANY liability for broken files the tool might create. Use at your own rist.

The tool MUST be used only for personal data or data you have permission to use.

# How to use
## C#/.NET
1. open the solution file in `cs_desktop` and build the application project
2. insert the password (you can get that by reverse engineering)
3. Use the GUI to perfrom en/decryption
## Java
1. run Maven with goal "package"
2. insert the password (you can get that by reverse engineering)
3. insert absolute path to file or directory (processing every file on its own)
4. hit encrypt / decrypt

## Android
* Supporting Android 8/8.1/9
* Crypto should be identically with java_desktop
* below 8 would need different Crypto as java_desktop, some methods aren't available
* Android 10+ needs Android SAF, java.io.File doesn't work on sdcard (short explanation), DocumentFile is needed everywhere. The GUI/Activity is prepared, see TODO comments.

# Java version linux / mac
Currently only windows x64 dependencies are added of swt, maybe you need:
[Maven SWT Linux ](https://mvnrepository.com/artifact/org.eclipse.swt/org.eclipse.swt.gtk.linux.x86_64/4.3)
[Maven SWT Mac](https://mvnrepository.com/artifact/org.eclipse.swt/org.eclipse.swt.cocoa.macosx.x86_64/4.3)
