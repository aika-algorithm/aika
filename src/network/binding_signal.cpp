#include "network/binding_signal.h"

BindingSignal::BindingSignal(int tokenId, Document* doc) : tokenId(tokenId), doc(doc) {}

int BindingSignal::getTokenId() const {
    return tokenId;
}

Document* BindingSignal::getDocument() const {
    return doc;
}

void BindingSignal::addActivation(Activation* act) {
    activations[act->getKey()] = act;
}

std::set<Activation*> BindingSignal::getActivations(Neuron* n) {
    std::set<Activation*> result;
    ActivationKey lowerBound(n->getId(), std::numeric_limits<int>::min());
    ActivationKey upperBound(n->getId(), std::numeric_limits<int>::max());
    for (auto it = activations.lower_bound(lowerBound); it != activations.upper_bound(upperBound); ++it) {
        result.insert(it->second);
    }
    return result;
}

std::set<Activation*> BindingSignal::getActivations() {
    std::set<Activation*> result;
    for (const auto& pair : activations) {
        result.insert(pair.second);
    }
    return result;
}

std::string BindingSignal::toString() const {
    return std::to_string(tokenId);
} 