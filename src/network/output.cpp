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

int NetworkOutput::transition(int s, Transition* trns) {
    return trns->to();
}

int NetworkOutput::getOrder() {
    return 1;
}

std::string NetworkOutput::toString() {
    return "OUTPUT";
} 