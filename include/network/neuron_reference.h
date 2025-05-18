#ifndef NETWORK_NEURON_REFERENCE_H
#define NETWORK_NEURON_REFERENCE_H

#include "ref_type.h"
#include "model.h"
// Forward declaration to break circular dependency
class Neuron;

class NeuronReference {
public:
    NeuronReference(long neuronId, RefType refType);
    NeuronReference(Neuron* n, RefType refType);

    long getId() const;
    Neuron* getRawNeuron() const;

    // Non-template method for backward compatibility
    Neuron* getNeuron(Model* m);
    
    // Template method for different neuron types
    template <typename N>
    N* getNeuron(Model* m);

    void suspendNeuron();

    std::string toString() const;
    std::string toKeyString() const;

private:
    long id;
    RefType refType;
    Neuron* neuron;
};

#endif // NETWORK_NEURON_REFERENCE_H 