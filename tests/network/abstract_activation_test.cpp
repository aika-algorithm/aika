#include "abstract_activation_test.h"

void AbstractActivationTest::setUp() {
    typeRegistry = new TypeRegistry();
    model = new Model(typeRegistry);
    
    // Create neuron and activation definitions
    neuronDef = new NeuronDefinition(typeRegistry, "test");
    activationDef = new ActivationDefinition(typeRegistry, "test_activation");
    neuronDef->setActivation(activationDef);
    
    // Initialize flattened types
    typeRegistry->flattenTypeHierarchy();
    
    // Create neuron instance
    neuron = neuronDef->instantiate(model);
    
    // Create document
    doc = new Document(model);
}

void AbstractActivationTest::tearDown() {
    delete doc;
    delete neuron;
    delete activationDef;
    delete neuronDef;
    delete model;
    delete typeRegistry;
}