#ifndef LINK_LATENT_TEST_H
#define LINK_LATENT_TEST_H

#include "abstract_activation_test.h"
#include "network/linker.h"
#include "network/synapse.h"
#include "network/types/synapse_type.h"
#include "network/types/link_type.h"
#include "network/builders/neuron_type_builder.h"
#include "network/builders/synapse_type_builder.h"
#include "network/binding_signal.h"
#include "network/transition.h"
#include <map>
#include <set>
#include <cassert>
#include <iostream>

class LinkLatentTest : public AbstractActivationTest {
private:
    // Test fixtures for linkLatent testing
    NeuronType* firstInputNeuronType;
    NeuronType* secondInputNeuronType;
    NeuronType* outputNeuronType;
    
    Neuron* firstInputNeuron;
    Neuron* secondInputNeuron;
    Neuron* outputNeuron;
    
    SynapseType* firstSynapseType;
    SynapseType* secondSynapseType;
    
    Synapse* firstSynapse;
    Synapse* secondSynapse;
    
    Activation* firstInputActivation;
    Activation* secondInputActivation;
    Activation* outputActivation;
    
    BindingSignal* bindingSignal1;
    BindingSignal* bindingSignal2;

protected:
    void setUpLinkLatentFixtures();
    void tearDownLinkLatentFixtures();

public:
    void testLinkLatentBasicFlow();
    void testActivationPropagationWithTransition();
    void testLinkLatentWithEmptyBindingSignals();
    void testLinkLatentWithNoSecondInputCandidates();
    void testLinkLatentDuplicateLinkPrevention();
    void testLinkLatentWithNullActivation();
    
    void runAllTests();
};

#endif // LINK_LATENT_TEST_H