#ifndef CRYPTOGRAPHY_RC4_H
#define CRYPTOGRAPHY_RC4_H
#include <array>
#include <random>


class rc4 {
private:
    std::vector<uint8_t> key;
    void initState(std::array<uint8_t, 256> &state);

public:
    rc4(const std::vector<uint8_t> &useKey);
    std::vector<uint8_t> encryptRC4(const std::vector<uint8_t> & plainText);
};


#endif //CRYPTOGRAPHY_RC4_H