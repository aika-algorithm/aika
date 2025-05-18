#ifndef NETWORK_INPUT_H
#define NETWORK_INPUT_H

#include "network/direction.h"
#include <string>

class NetworkInput : public NetworkDirection {
public:
    NetworkDirection* invert() override;

    template <typename I>
    I getInput(I from, I to);

    template <typename O>
    O getOutput(O from, O to);

    Neuron* getNeuron(Model* m, Synapse* s) override;
    Activation* getActivation(Link* l) override;
    BSType* transition(BSType* s, Transition* trns) override;
    int getOrder() override;
//    void write(std::ostream& out) override;
    std::string toString();
};

#endif // NETWORK_INPUT_H 