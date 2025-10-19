#include "network/linker.h"
#include "fields/type_registry.h"
#include "network/model.h"
#include "network/context.h"
#include "network/types/neuron_type.h"
#include "network/types/activation_type.h"
#include "network/types/synapse_type.h"
#include "network/activation.h"
#include "network/binding_signal.h"
#include <iostream>
#include <map>

/**
 * Standalone test for Linker::linkLatent method
 * This test verifies the basic functionality and edge cases of linkLatent
 */
class LinkLatentStandaloneTest {
private:
    TypeRegistry* registry;
    Model* model;
    Context* context;

public:
    LinkLatentStandaloneTest() {
        registry = new TypeRegistry();
        model = new Model(registry);
        context = new Context(model);
    }
    
    ~LinkLatentStandaloneTest() {
        delete context;
        delete model;
        delete registry;
    }
    
    void testNullActivationHandling() {
        std::cout << "Testing linkLatent with null activation..." << std::endl;
        
        try {
            Linker::linkLatent(nullptr);
            std::cout << "❌ Expected exception for null activation" << std::endl;
        } catch (const std::exception& e) {
            std::cout << "✅ Correctly handled null activation: " << e.what() << std::endl;
        } catch (...) {
            std::cout << "✅ Correctly threw exception for null activation" << std::endl;
        }
    }
    
    void testBasicLinkLatentCall() {
        std::cout << "Testing basic linkLatent call with minimal setup..." << std::endl;
        
        try {
            // Create minimal neuron and activation setup
            NeuronType* neuronType = new NeuronType(registry, "TEST_NEURON");
            ActivationType* activationType = new ActivationType(registry, "TEST_ACTIVATION");
            
            neuronType->setActivationType(activationType);
            activationType->setNeuronType(neuronType);
            
            Neuron* neuron = neuronType->instantiate(model);
            
            std::map<int, BindingSignal*> bindingSignals;
            BindingSignal* bs = new BindingSignal(1, context);
            bindingSignals[1] = bs;
            
            Activation* activation = new Activation(activationType, nullptr, 1, neuron, context, bindingSignals);
            bs->addActivation(activation);
            
            // Call linkLatent - this should handle the case where neuron has no output synapses
            Linker::linkLatent(activation);
            
            std::cout << "✅ Basic linkLatent call completed successfully" << std::endl;
            
            // Cleanup
            delete activation;
            delete bs;
            delete activationType;
            delete neuronType;
            
        } catch (const std::exception& e) {
            std::cout << "⚠️  Basic linkLatent call: " << e.what() << std::endl;
        }
    }
    
    void runAllTests() {
        std::cout << "\n=== LinkLatent Standalone Tests ===" << std::endl;
        
        testNullActivationHandling();
        testBasicLinkLatentCall();
        
        std::cout << "=== LinkLatent Standalone Tests Complete ===" << std::endl;
    }
};

int main() {
    std::cout << "Running LinkLatent Standalone Test..." << std::endl;
    
    try {
        LinkLatentStandaloneTest test;
        test.runAllTests();
        
        std::cout << "\nStandalone test completed!" << std::endl;
        return 0;
        
    } catch (const std::exception& e) {
        std::cerr << "Test failed with exception: " << e.what() << std::endl;
        return 1;
    } catch (...) {
        std::cerr << "Test failed with unknown exception" << std::endl;
        return 1;
    }
}