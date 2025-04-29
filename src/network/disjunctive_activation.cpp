#include "network/disjunctive_activation.h"

DisjunctiveActivation::DisjunctiveActivation(ActivationDefinition* t, Activation* parent, int id, Neuron* n, Document* doc, std::map<BSType, BindingSignal*> bindingSignals)
    : Activation(t, parent, id, n, doc, bindingSignals) {}

DisjunctiveActivation::~DisjunctiveActivation() {}

void DisjunctiveActivation::linkIncoming(Activation* excludedInputAct) {
    // Implementation for linking incoming activations
}

void DisjunctiveActivation::addInputLink(Link* l) {
    Activation* iAct = l->getInput();
    assert(inputLinks.find(iAct->getId()) == inputLinks.end());
    inputLinks[iAct->getId()] = l;
}

std::vector<Link*> DisjunctiveActivation::getInputLinks() {
    std::vector<Link*> result;
    for (const auto& pair : inputLinks) {
        result.push_back(pair.second);
    }
    return result;
} 