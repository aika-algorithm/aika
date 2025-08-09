#ifndef ABSTRACT_ACTIVATION_TEST_H
#define ABSTRACT_ACTIVATION_TEST_H

#include "fields/type_registry.h"
#include "network/model.h"
#include "network/context.h"
#include "network/neuron.h"
#include "network/neuron_type.h"
#include "network/activation_type.h"

class AbstractActivationTest {
protected:
    TypeRegistry* typeRegistry;
    Model* model;
    NeuronType* neuronDef;
    ActivationType* activationType;
    Neuron* neuron;
    Context* ctx;

    void setUp();
    void tearDown();

public:
    virtual ~AbstractActivationTest() = default;
};

#endif // ABSTRACT_ACTIVATION_TEST_H