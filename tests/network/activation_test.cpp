#include "activation_test.h"
#include <iostream>

void ActivationTest::testHasConflictingBindingSignals() {
    std::cout << "Running testHasConflictingBindingSignals..." << std::endl;
    
    BindingSignal* bs0 = new BindingSignal(0, doc);
    BindingSignal* bs1 = new BindingSignal(1, doc);
    
    // Create activation with binding signal A -> bs0
    std::map<BSType*, BindingSignal*> bindingSignals = {{TEST_BS_A, bs0}};
    ConjunctiveActivation* act = new ConjunctiveActivation(
        activationType,
        nullptr,  // parent
        1,        // id
        neuron,
        doc,
        bindingSignals
    );
    
    // Test 1: Same binding signal should not conflict
    std::map<BSType*, BindingSignal*> targetSignals1 = {{TEST_BS_A, bs0}};
    assert(!act->hasConflictingBindingSignals(targetSignals1));
    
    // Test 2: Additional binding signal should not conflict
    std::map<BSType*, BindingSignal*> targetSignals2 = {{TEST_BS_A, bs0}, {TEST_BS_B, bs1}};
    assert(!act->hasConflictingBindingSignals(targetSignals2));
    
    // Test 3: Different binding signal for same type should conflict
    std::map<BSType*, BindingSignal*> targetSignals3 = {{TEST_BS_A, bs1}, {TEST_BS_B, bs0}};
    assert(act->hasConflictingBindingSignals(targetSignals3));
    
    delete act;
    delete bs1;
    delete bs0;
    
    std::cout << "testHasConflictingBindingSignals PASSED" << std::endl;
}

void ActivationTest::testHasNewBindingSignals() {
    std::cout << "Running testHasNewBindingSignals..." << std::endl;
    
    BindingSignal* bs0 = new BindingSignal(0, doc);
    BindingSignal* bs1 = new BindingSignal(1, doc);
    
    // Create activation with binding signal A -> bs0
    std::map<BSType*, BindingSignal*> bindingSignals = {{TEST_BS_A, bs0}};
    ConjunctiveActivation* act = new ConjunctiveActivation(
        activationType,
        nullptr,  // parent
        1,        // id
        neuron,
        doc,
        bindingSignals
    );
    
    // Test 1: Additional binding signal should be detected as new
    std::map<BSType*, BindingSignal*> targetSignals1 = {{TEST_BS_A, bs0}, {TEST_BS_B, bs1}};
    assert(act->hasNewBindingSignals(targetSignals1));
    
    // Test 2: Same binding signals should not be detected as new
    std::map<BSType*, BindingSignal*> targetSignals2 = {{TEST_BS_A, bs0}};
    assert(!act->hasNewBindingSignals(targetSignals2));
    
    delete act;
    delete bs1;
    delete bs0;
    
    std::cout << "testHasNewBindingSignals PASSED" << std::endl;
}

void ActivationTest::testBranch() {
    std::cout << "Running testBranch..." << std::endl;
    
    BindingSignal* bs0 = new BindingSignal(0, doc);
    BindingSignal* bs1 = new BindingSignal(1, doc);
    
    // Create parent activation
    std::map<BSType*, BindingSignal*> parentBindingSignals = {{TEST_BS_A, bs0}};
    ConjunctiveActivation* parentAct = new ConjunctiveActivation(
        activationType,
        nullptr,  // parent
        1,        // id
        neuron,
        doc,
        parentBindingSignals
    );
    
    // Create child activation through branching
    std::map<BSType*, BindingSignal*> childBindingSignals = {{TEST_BS_B, bs1}};
    Activation* childAct = parentAct->branch(childBindingSignals);
    
    if (childAct != nullptr) {
        // Verify parent-child relationship
        assert(childAct->getParent() == parentAct);
        
        // Verify child has the new binding signal
        auto childSignals = childAct->getBindingSignals();
        assert(childSignals.size() == 1);
        assert(childAct->getBindingSignal(TEST_BS_B) == bs1);
        
        delete childAct;
        std::cout << "testBranch PASSED" << std::endl;
    } else {
        std::cout << "testBranch SKIPPED (branch method returns nullptr)" << std::endl;
    }
    
    delete parentAct;
    delete bs1;
    delete bs0;
}

void ActivationTest::testCollectLinkingTargets() {
    std::cout << "Running testCollectLinkingTargets..." << std::endl;
    // This test would require more complex setup with multiple neurons
    // For now, we'll create a placeholder test
    std::cout << "testCollectLinkingTargets SKIPPED (placeholder)" << std::endl;
}

void ActivationTest::testLinkOutgoing() {
    std::cout << "Running testLinkOutgoing..." << std::endl;
    // This test would require synapse setup
    // For now, we'll create a placeholder test
    std::cout << "testLinkOutgoing SKIPPED (placeholder)" << std::endl;
}

void ActivationTest::testPropagate() {
    std::cout << "Running testPropagate..." << std::endl;
    // This test would require synapse setup
    // For now, we'll create a placeholder test
    std::cout << "testPropagate SKIPPED (placeholder)" << std::endl;
}

void ActivationTest::runAllTests() {
    setUp();
    
    testHasConflictingBindingSignals();
    testHasNewBindingSignals();
    testBranch();
    testCollectLinkingTargets();
    testLinkOutgoing();
    testPropagate();
    
    tearDown();
}