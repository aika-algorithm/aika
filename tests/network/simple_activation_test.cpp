#include <iostream>
#include <map>
#include <cassert>

// Forward declarations
class BSType {
public:
    virtual ~BSType() = default;
};

class TestBSTypeA : public BSType {
public:
    std::string getName() const { return "A"; }
};

class TestBSTypeB : public BSType {
public:
    std::string getName() const { return "B"; }
};

class BindingSignal {
private:
    int tokenId;
public:
    BindingSignal(int id) : tokenId(id) {}
    int getTokenId() const { return tokenId; }
};

// Simple test of activation logic without full object hierarchy
class SimpleActivationTest {
private:
    std::map<BSType*, BindingSignal*> bindingSignals;
    
public:
    SimpleActivationTest(const std::map<BSType*, BindingSignal*>& signals) 
        : bindingSignals(signals) {}
    
    bool hasConflictingBindingSignals(const std::map<BSType*, BindingSignal*>& targetBindingSignals) const {
        for (const auto& targetPair : targetBindingSignals) {
            auto it = bindingSignals.find(targetPair.first);
            if (it != bindingSignals.end() && it->second != targetPair.second) {
                return true; // Conflicting binding signal found
            }
        }
        return false;
    }
    
    bool hasNewBindingSignals(const std::map<BSType*, BindingSignal*>& targetBindingSignals) const {
        for (const auto& targetPair : targetBindingSignals) {
            if (bindingSignals.find(targetPair.first) == bindingSignals.end()) {
                return true; // New binding signal found
            }
        }
        return false;
    }
    
    std::map<BSType*, BindingSignal*> getBindingSignals() const {
        return bindingSignals;
    }
};

void testHasConflictingBindingSignals() {
    std::cout << "Running testHasConflictingBindingSignals..." << std::endl;
    
    TestBSTypeA* bsTypeA = new TestBSTypeA();
    TestBSTypeB* bsTypeB = new TestBSTypeB();
    
    BindingSignal* bs0 = new BindingSignal(0);
    BindingSignal* bs1 = new BindingSignal(1);
    
    // Create activation with binding signal A -> bs0
    std::map<BSType*, BindingSignal*> bindingSignals = {{bsTypeA, bs0}};
    SimpleActivationTest act(bindingSignals);
    
    // Test 1: Same binding signal should not conflict
    std::map<BSType*, BindingSignal*> targetSignals1 = {{bsTypeA, bs0}};
    assert(!act.hasConflictingBindingSignals(targetSignals1));
    
    // Test 2: Additional binding signal should not conflict
    std::map<BSType*, BindingSignal*> targetSignals2 = {{bsTypeA, bs0}, {bsTypeB, bs1}};
    assert(!act.hasConflictingBindingSignals(targetSignals2));
    
    // Test 3: Different binding signal for same type should conflict
    std::map<BSType*, BindingSignal*> targetSignals3 = {{bsTypeA, bs1}, {bsTypeB, bs0}};
    assert(act.hasConflictingBindingSignals(targetSignals3));
    
    delete bs1;
    delete bs0;
    delete bsTypeB;
    delete bsTypeA;
    
    std::cout << "testHasConflictingBindingSignals PASSED" << std::endl;
}

void testHasNewBindingSignals() {
    std::cout << "Running testHasNewBindingSignals..." << std::endl;
    
    TestBSTypeA* bsTypeA = new TestBSTypeA();
    TestBSTypeB* bsTypeB = new TestBSTypeB();
    
    BindingSignal* bs0 = new BindingSignal(0);
    BindingSignal* bs1 = new BindingSignal(1);
    
    // Create activation with binding signal A -> bs0
    std::map<BSType*, BindingSignal*> bindingSignals = {{bsTypeA, bs0}};
    SimpleActivationTest act(bindingSignals);
    
    // Test 1: Additional binding signal should be detected as new
    std::map<BSType*, BindingSignal*> targetSignals1 = {{bsTypeA, bs0}, {bsTypeB, bs1}};
    assert(act.hasNewBindingSignals(targetSignals1));
    
    // Test 2: Same binding signals should not be detected as new
    std::map<BSType*, BindingSignal*> targetSignals2 = {{bsTypeA, bs0}};
    assert(!act.hasNewBindingSignals(targetSignals2));
    
    delete bs1;
    delete bs0;
    delete bsTypeB;
    delete bsTypeA;
    
    std::cout << "testHasNewBindingSignals PASSED" << std::endl;
}

int main() {
    std::cout << "Running Simple Activation Tests..." << std::endl;
    
    try {
        testHasConflictingBindingSignals();
        testHasNewBindingSignals();
        
        std::cout << "\nAll simple tests completed successfully!" << std::endl;
        return 0;
    } catch (const std::exception& e) {
        std::cerr << "Test failed with exception: " << e.what() << std::endl;
        return 1;
    } catch (...) {
        std::cerr << "Test failed with unknown exception" << std::endl;
        return 1;
    }
}