
#ifndef CRYPTOGRAPHY_CIPHER_H
#define CRYPTOGRAPHY_CIPHER_H
#include <random>
#include <string>


namespace std {
    enum class byte : unsigned char;
}

class cipher {
private:
    std::array <uint8_t, 8> key = {0, 0, 0, 0, 0, 0, 0, 0};
    std::mt19937 currentSeed;
    std::array<std::array<uint8_t, 256>, 8> subTables;
    std::array<std::array<uint8_t, 256>, 8> subTablesReverse;

    static std::array<uint8_t, 256> makeSingleTable();
    std::array<uint8_t, 256> shuffleSingleTable(const std::array<uint8_t, 256> & fullArray);
    std::array<std::array<uint8_t, 256>, 8> makeEightTables();
    void makeReverseTables();

public:
    cipher();

    void generateKey(std::string & password);
    std::mt19937 generateSeed();

    std::array<uint8_t, 8> encrypt(std::array<uint8_t, 8> & input);
    std::array<uint8_t, 8> decrypt(std::array<uint8_t, 8> & input);
    void slideLeft(std::array<uint8_t, 8> & block);
    void slideRight(std::array<uint8_t, 8> & block);


    std::array <uint8_t, 8> acceptInput();
    std::array <uint8_t, 8> scrambleSingle();
    std::array<std::array<uint8_t, 8>, 16> scrambleAll();
};


#endif //CRYPTOGRAPHY_CIPHER_H