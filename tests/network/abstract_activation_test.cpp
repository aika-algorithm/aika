#include "abstract_activation_test.h"

void AbstractActivationTest::setUp() {
    typeRegistry = new TypeRegistry();
    model = new Model(typeRegistry);
    
    // Create neuron and activation definitions
    neuronDef = new NeuronType(typeRegistry, "test");
    activationType = new ActivationType(typeRegistry, "test_activation");
    neuronDef->setActivation(activationType);
    
    // Initialize flattened types
    typeRegistry->flattenTypeHierarchy();
    
    // Create neuron instance
    neuron = neuronDef->instantiate(model);
    
    // Create context
    ctx = new Context(model);
}

void AbstractActivationTest::tearDown() {
    delete ctx;
    delete neuron;
    delete activationType;
    delete neuronDef;
    delete model;
    delete typeRegistry;
}