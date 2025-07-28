#ifndef TEST_BS_TYPES_H
#define TEST_BS_TYPES_H

#include "network/bs_type.h"
#include <string>

// Test BSType implementations for C++ tests
class TestBSTypeA : public BSType {
public:
    std::string getName() const { return "A"; }
};

class TestBSTypeB : public BSType {
public:
    std::string getName() const { return "B"; }
};

// Global test instances
extern TestBSTypeA* TEST_BS_A;
extern TestBSTypeB* TEST_BS_B;

#endif // TEST_BS_TYPES_H