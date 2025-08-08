#ifndef ACTIVATION_TEST_H
#define ACTIVATION_TEST_H

#include "abstract_activation_test.h"
#include "network/activation.h"
#include "network/binding_signal.h"
#include <map>
#include <cassert>

class ActivationTest : public AbstractActivationTest {
public:
    void testCollectLinkingTargets();
    void testLinkOutgoing();
    void testPropagate();
    
    void runAllTests();
};

#endif // ACTIVATION_TEST_H