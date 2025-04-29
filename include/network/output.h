#ifndef NETWORK_OUTPUT_H
#define NETWORK_OUTPUT_H

#include "network/direction.h"
#include "network/model.h"
#include "network/activation.h"
#include "network/link.h"
#include "network/neuron.h"
#include "network/synapse.h"
#include "network/bs_type.h"
#include "network/transition.h"

#include <string>

class Output : public Direction {
public:
    Direction* invert() override;

    template <typename I>
    I getInput(I from, I to) override;

    template <typename O>
    O getOutput(O from, O to) override;

    Neuron* getNeuron(Model* m, Synapse* s) override;
    Activation* getActivation(Link* l) override;
    BSType* transition(BSType* s, Transition* trns) override;
    int getOrder() override;
    void write(std::ostream& out) override;
    std::string toString() override;
};

#endif // NETWORK_OUTPUT_H 