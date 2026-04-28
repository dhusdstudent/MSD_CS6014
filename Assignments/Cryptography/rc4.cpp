#include "rc4.h"

#include <array>
#include <cstdint>
#include <filesystem>
#include <iostream>

rc4::rc4(const std::vector<uint8_t> &useKey)
: key(useKey) {}


void rc4::initState(std::array<uint8_t, 256> &stateArray) {
    for (int i = 0; i < 256; i++) {
        stateArray[i] = i;
    }

    int swapVar1 = 0;

    for (int i = 0; i < 256; i++) {
        swapVar1 = (swapVar1 + stateArray[i] + key[i % key.size()]) % 256;
        std::swap(stateArray[i], stateArray[swapVar1]);
    }
}

std::vector<uint8_t> rc4::encryptRC4(const std::vector<uint8_t> & plainText) {

    std::array<uint8_t, 256> stateArray;
    initState(stateArray);

    int swapVar1 = 0;
    int swapVar2 = 0;

    std::vector<uint8_t> cipherText(plainText.size());

    for (int i = 0; i < plainText.size(); i++) {
        swapVar1 = (swapVar1 + 1) % 256;
        swapVar2 = (swapVar2 + stateArray[swapVar1]) % 256;

        std::swap(stateArray[swapVar1], stateArray[swapVar2]);

        uint8_t keyStream = stateArray[(stateArray[swapVar1] + stateArray[swapVar2]) % 256];

        cipherText[i] = plainText[i] xor keyStream;
    }
    return cipherText;
}
