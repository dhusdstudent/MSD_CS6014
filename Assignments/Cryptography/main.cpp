#include <iostream>

#include "cipher.h"
#include "rc4.h"

void useRC4Good();
void badCipherExample();
void goodCipherExample();
void useRCBad();
void rc4KeystreamReuseExample();
void rc4BitFlippingAttack();

int main() {
    //useRC4Good();
    //useRCBad();
    //rc4KeystreamReuseExample();
    //goodCipherExample();
    //badCipherExample();
     //rc4BitFlippingAttack();

return 0;
}



void useRC4Good() {
    std::vector<uint8_t> key = {'M', 'y', 'k', 'e', 'y'};
    rc4 cipher(key);
    std::vector<uint8_t> plainText = {'S', 'e', 'c', 'r', 'e', 't', ' ', 'm', 'e', 's', 's', 'a', 'g', 'e'};

    auto cipherText = cipher.encryptRC4(plainText);
    auto decryptText = cipher.encryptRC4(cipherText);

    std::cout << "Alice sent the message: ";
    for (auto text: plainText) {
        std::cout << (char)text;
    }

    std::cout << std::endl;

    std::cout << "Mallory saw the message: ";
    for (auto text: cipherText) {
        std::cout << (char)text;
    }

    std::cout << std::endl;

    std::cout << "Bob received the message: ";
    for (auto text: decryptText) {
        std::cout << (char)text;
    }
}

void useRCBad() {
    std::vector<uint8_t> key = {'M', 'y', 'k', 'e', 'y'};
    rc4 correctCipher(key);
    std::vector<uint8_t> plainText = {'S', 'e', 'c', 'r', 'e', 't', ' ', 'm', 'e', 's', 's', 'a', 'g', 'e'};

    auto cipherText = correctCipher.encryptRC4(plainText);

    std::vector<uint8_t> wrongKey = {'B','a','d'};
    rc4 wrongCipher(wrongKey);

    auto decryptText = wrongCipher.encryptRC4(cipherText);

    std::cout << "Alice sent the message: ";
    for (auto text: plainText) {
        std::cout << (char)text;
    }

    std::cout << std::endl;

    std::cout << "Mallory saw the message: ";
    for (auto text: cipherText) {
        std::cout << (char)text;
    }

    std::cout << std::endl;

    std::cout << "Bob received the message: ";
    for (auto text: decryptText) {
        std::cout << (char)text;
    }
}

void goodCipherExample() {
    cipher myCipher;

    std::string password = "hello";
    myCipher.generateKey(password);

    std::array<uint8_t, 8> normalMsg = {1, 2, 3, 4, 5, 6, 7, 8};

    auto cipherText = myCipher.encrypt(normalMsg);
    auto decryptText = myCipher.decrypt(cipherText);

    std::cout << "Alice sent the message: ";
    for (auto text: normalMsg) {
        std::cout << (int)text << " ";
    }

    std::cout << std::endl;

    std::cout << "Mallory saw the message: ";
    for (auto text: cipherText) {
        std::cout << (int)text << " ";
    }

    std::cout << std::endl;

    std::cout << "Bob received the message: ";
    for (auto text: decryptText) {
        std::cout << (int)text << " ";
    }

    std::cout << std::endl;
}

void badCipherExample() {
    cipher myCipher;

    std::string password = "hello";
    myCipher.generateKey(password);

    std::array<uint8_t, 8> normalMsg = {1, 2, 3, 4, 5, 6, 7, 8};

    auto cipherText = myCipher.encrypt(normalMsg);
    auto decryptText = myCipher.decrypt(cipherText);

    cipher wrongCipher;
    std::string wrongPass = "bye";
    wrongCipher.generateKey(wrongPass);
    auto wrongDecrypted = wrongCipher.decrypt(cipherText);

    std::cout << "Wrong password decryption: ";
    for (auto b : wrongDecrypted) std::cout << (int)b << " ";
    std::cout << "\n";

    auto modifiedCipher = cipherText;
    modifiedCipher[0] ^= 0x01;
    auto avalancheTest = myCipher.decrypt(modifiedCipher);
    //WHAT DO YOU SEE?
    //Flipping one bit can result in a lot more than one bit of change within the output. It can be entirely different text.

    std::cout << "Bit-flipped decryption: ";
    for (auto b : avalancheTest) std::cout << (int)b << " ";
    std::cout << "\n";

}

void rc4KeystreamReuseExample() {
    std::vector<uint8_t> key = {'S', 'a', 'm', 'e', 'K', 'e', 'y'};
    rc4 cipher(key);

    std::vector<uint8_t> msg1 = {'M', 'e', 'e', 't', 'm', 'e', ' ', 'a', 't', ' ', 'n', 'o', 'o', 'n'};


    std::vector<uint8_t> msg2 = {'A', 't', ' ', 't', 'h', 'e', ' ', 'b', 'r', 'i', 'd', 'g', 'e'};

    auto c1 = cipher.encryptRC4(msg1);
    auto c2 = cipher.encryptRC4(msg2);

    std::cout << "Ciphertext1 xor'd with Ciphertext2 reveals...\n";

    for (size_t i = 0; i < c1.size() && i < c2.size(); i++) {
        char xored = c1[i] ^ c2[i];
        std::cout << xored;
    }
    std::cout << std::endl;
}

void rc4BitFlippingAttack() {
    std::vector<uint8_t> key = {'M','o','n','e','y'};
    rc4 cipher(key);

    std::string original = "Your salary is $1000";
    std::vector<uint8_t> message(original.begin(), original.end());

    auto cipherText = cipher.encryptRC4(message);
    size_t position = original.find('1');

    if (position != std::string::npos) {
        cipherText[position] ^= ('1' ^ '9');
    }

    rc4 decryptCipher(key);
    auto modifiedPlain = decryptCipher.encryptRC4(cipherText);

    std::cout << "Original message: " << original << std::endl;

    std::cout << "Modified message: ";
    for (auto c : modifiedPlain)
        std::cout << (char)c;

    std::cout << std::endl;
}