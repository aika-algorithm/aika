#include "network/output.h"
#include "network/input.h"

NetworkDirection* NetworkOutput::invert() {
    return NetworkDirection::INPUT;
}

Neuron* NetworkOutput::getNeuron(Model* m, Synapse* s) {
    return s->getOutput(m);
}

Activation* NetworkOutput::getActivation(Link* l) {
    return l ? l->getOutput() : nullptr;
}

BSType* NetworkOutput::transition(BSType* s, Transition* trns) {
    // Transition has from() and to() methods but isn't an array
    // Check if the 'from' matches our input
    if (trns->from() == s) {
        return trns->to();
    }
    return nullptr;
}

int NetworkOutput::getOrder() {
    return 1;
}

/*
void NetworkOutput::write(std::ostream& out) {
    out << true;
}
*/

std::string NetworkOutput::toString() {
    return "OUTPUT";
} 