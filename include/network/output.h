#ifndef NETWORK_OUTPUT_H
#define NETWORK_OUTPUT_H

#include "network/direction.h"
#include "network/model.h"
#include "network/activation.h"
#include "network/link.h"
#include "network/neuron.h"
#include "network/synapse.h"
#include "network/transition.h"

#include <string>

class NetworkOutput : public NetworkDirection {
public:
    NetworkDirection* invert() override;

    template <typename I>
    I getInput(I from, I to);

    template <typename O>
    O getOutput(O from, O to);

    Neuron* getNeuron(Model* m, Synapse* s) override;
    Activation* getActivation(Link* l) override;
    int transition(int s, Transition* trns) override;
    int getOrder() override;
//    void write(std::ostream& out) override;
    std::string toString();
};

#endif // NETWORK_OUTPUT_H 