#include "network/input.h"
#include "network/neuron.h"
#include "network/synapse.h"
#include "network/binding_signal.h"
#include "network/transition.h"

Direction* Input::invert() {
    return OUTPUT;
}

template <typename I>
I Input::getInput(I from, I to) {
    return to;
}

template <typename O>
O Input::getOutput(O from, O to) {
    return from;
}

Neuron* Input::getNeuron(Model* m, Synapse* s) {
    return s->getInput(m);
}

Activation* Input::getActivation(Link* l) {
    return l ? l->getInput() : nullptr;
}

BSType* Input::transition(BSType* s, Transition* trns) {
    for (auto& t : trns) {
        if (t.to() == s) {
            return t.from();
        }
    }
    return nullptr;
}

int Input::getOrder() {
    return -1;
}

void Input::write(std::ostream& out) {
    out << false;
}

std::string Input::toString() {
    return "INPUT";
} 