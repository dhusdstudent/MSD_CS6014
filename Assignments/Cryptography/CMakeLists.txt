cmake_minimum_required(VERSION 4.1)
project(Cryptography)

set(CMAKE_CXX_STANDARD 20)

add_executable(Cryptography main.cpp
        cipher.cpp
        cipher.h
        rc4.cpp
        rc4.h)
