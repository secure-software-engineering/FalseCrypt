# FalseCrypt - How NOT to do encryption
FalseCrypt is demonstration project for the Roslyn-based static code analysis tool Sharper Crypto-API Analysis.

# Purpose
FalseCrypt is a file encryption tool like TrueCrypt or BitDefender. Its code contains many weaknesses like the insecure usage of the old DES block cipher. The project was developed to demonstrate the code analysis findings offered by Sharper Crypto-API Analysis. 

# Disclaimer
By any means the source code of this project does NOT contain any good usage of the .NET Crypto API. It should therefore not be used for any serious production code. 
