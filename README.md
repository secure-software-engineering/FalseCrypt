# FalseCrypt - How NOT to do encryption
FalseCrypt is demonstration project for the Roslyn-based static code analysis tool Sharper Crypto-API Analysis.

# Purpose
FalseCrypt is a file encryption tool like TrueCrypt or BitDefender. Its code contains many weaknesses like the insecure usage of the old DES block cipher. The project was developed to demonstrate the code analysis findings offered by Sharper Crypto-API Analysis. 

# Disclaimer
By any means the source code of this project does NOT contain any good usage of the .NET Crypto API. It should therefore not be used for any serious production code. 

# Java version linux / mac
Currently only windows x64 dependencies are added of swt, maybe you need:
[Maven SWT Linux ](https://mvnrepository.com/artifact/org.eclipse.swt/org.eclipse.swt.gtk.linux.x86_64/4.3)
[Maven SWT Mac](https://mvnrepository.com/artifact/org.eclipse.swt/org.eclipse.swt.cocoa.macosx.x86_64/4.3)

# How to use
1. run Maven with goal "package"
2. insert password
3. insert absolute path to file or directory (processing every file on its own)
4. hit encrypt / decrypt