#include "network/output.h"

Direction* Output::invert() {
    return INPUT;
}

Neuron* Output::getNeuron(Model* m, Synapse* s) {
    return s->getOutput(m);
}

Activation* Output::getActivation(Link* l) {
    return l ? l->getOutput() : nullptr;
}

BSType* Output::transition(BSType* s, Transition* trns) {
    for (auto& t : trns) {
        if (t.from() == s) {
            return t.to();
        }
    }
    return nullptr;
}

int Output::getOrder() {
    return 1;
}

void Output::write(std::ostream& out) {
    out << true;
}

std::string Output::toString() {
    return "OUTPUT";
} 