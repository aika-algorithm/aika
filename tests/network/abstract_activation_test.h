#ifndef ABSTRACT_ACTIVATION_TEST_H
#define ABSTRACT_ACTIVATION_TEST_H

#include "fields/type_registry.h"
#include "network/model.h"
#include "network/document.h"
#include "network/neuron.h"
#include "network/neuron_definition.h"
#include "network/activation_definition.h"
#include "test_bs_types.h"

class AbstractActivationTest {
protected:
    TypeRegistry* typeRegistry;
    Model* model;
    NeuronDefinition* neuronDef;
    ActivationDefinition* activationDef;
    Neuron* neuron;
    Document* doc;

    void setUp();
    void tearDown();

public:
    virtual ~AbstractActivationTest() = default;
};

#endif // ABSTRACT_ACTIVATION_TEST_H