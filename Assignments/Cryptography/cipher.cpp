
#include "cipher.h"

#include <iostream>
#include <random>
#include <vector>
#include <array>
#include <cstdint>
#include <chrono>
#include <algorithm>

//--------------------------------------------------SETUP------------------------------------

cipher::cipher() {
    currentSeed = generateSeed();
    subTables = makeEightTables();
    makeReverseTables();
};

//This is bad for a lot of reasons! It's reversible, it can result in collisions, and you'd be
//able to see patterns.
void cipher::generateKey(std::string & password) {
    key.fill(0);
    for (int i = 0; i < password.length(); i++) {
        key[i%8] = key[i%8] xor password[i];
    }
}

std::mt19937 cipher::generateSeed() {
    unsigned seed = std::chrono::system_clock::now().time_since_epoch().count();
    std::mt19937 rng(seed);
    return rng;
}

//--------------------------------------------------MOVEMENT------------------------------------

void cipher::slideLeft(std::array<uint8_t, 8> & block) {
    uint8_t firstBit = (block[0] & 0x80) >> 7;

    for (int i = 0; i < 7; i++) {
        uint8_t nextBit = (block[i + 1] & 0x80) >> 7;
        block[i] = (block[i] << 1) | nextBit;
    }
    block[7] = block[7] << 1 | firstBit;
}

void cipher::slideRight(std::array<uint8_t, 8> & block) {
    uint8_t lastBit = (block[7] & 0x01);

    for (int i = 7; i > 0; i--) {
        uint8_t nextBit = block[i - 1] & 0x01;
        block[i] = (block[i] >> 1) | (nextBit << 7);
    }
    block[0] = block[0] >> 1 | (lastBit << 7);
}

//----------------------------------------CRYPTOGRAPHY------------------------------------

std::array<uint8_t, 8> cipher::encrypt(std::array<uint8_t, 8> & input) {
    std::array<uint8_t, 8> state = input;

    for (int i = 0; i < 16; i++) {
        for (int j = 0; j < 8; j++) {
            state[j] ^= key[j];
        }

        for (int k = 0; k < 8; k++) {
            state[k] = subTables[k][state[k]];
        }

        slideLeft(state);
    }
        return state;
}

std::array<uint8_t, 8> cipher::decrypt(std::array<uint8_t, 8> & input) {
    std::array<uint8_t, 8> state = input;

    for (int i = 0; i < 16; i++) {
        slideRight(state);

        for (int j = 0; j < 8; j++) {
            state[j] = subTablesReverse[j][state[j]];
        }

        for (int k = 0; k < 8; k++) {
            state[k] ^= key[k];
        }
    }
    return state;
}

//--------------------------------------------------TABLES------------------------------------

void cipher::makeReverseTables() {
    for (int i = 0; i < 8; i++) {
        for (int j = 0; j < 256; j++) {
            uint8_t val = subTables[i][j];
            subTablesReverse[i][val] = j;
        }
    }
}

std::array<uint8_t, 256> cipher::makeSingleTable() {
    std::array<uint8_t, 256> table{};
    for (int i = 0; i < 256; i++) {
        table[i] = i;
    }
    return table;
}

std::array<uint8_t, 256> cipher::shuffleSingleTable(const std::array<uint8_t, 256> & fullArray) {
    std::array<uint8_t, 256> table = fullArray;
    std::ranges::shuffle(table, currentSeed);
    return table;
}

std::array<std::array<uint8_t, 256>, 8> cipher::makeEightTables() {
    std::array<std::array<uint8_t, 256>, 8> table{};
    for (int i = 0; i < 8; i++) {
        table[i] = shuffleSingleTable(makeSingleTable());
    }
    return table;
}


