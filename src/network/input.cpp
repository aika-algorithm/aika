#include "network/input.h"
#include "network/output.h"
#include "network/neuron.h"
#include "network/synapse.h"
#include "network/binding_signal.h"
#include "network/transition.h"

NetworkDirection* NetworkInput::invert() {
    static NetworkOutput output;
    return &output;
}

template <typename I>
I NetworkInput::getInput(I from, I to) {
    return to;
}

template <typename O>
O NetworkInput::getOutput(O from, O to) {
    return from;
}

Neuron* NetworkInput::getNeuron(Model* m, Synapse* s) {
    return s->getInput(m);
}

Activation* NetworkInput::getActivation(Link* l) {
    return l ? l->getInput() : nullptr;
}

BSType* NetworkInput::transition(BSType* s, Transition* trns) {
    // Transition is not an iterable type, we need to handle it differently
    if (trns) {
        return trns->from();
    }
    return nullptr;
}

int NetworkInput::getOrder() {
    return -1;
}

/*
void NetworkInput::write(std::ostream& out) {
    out << false;
}
*/

std::string NetworkInput::toString() {
    return "INPUT";
} 