#include "network/conjunctive_activation.h"
#include "network/synapse.h"
#include "network/synapse_definition.h"

ConjunctiveActivation::ConjunctiveActivation(ActivationDefinition* t, Activation* parent, int id, Neuron* n, Document* doc, std::map<BSType, BindingSignal*> bindingSignals)
    : Activation(t, parent, id, n, doc, bindingSignals) {}

ConjunctiveActivation::~ConjunctiveActivation() {}

void ConjunctiveActivation::linkIncoming(Activation* excludedInputAct) {
    for (auto& s : neuron->getInputSynapsesAsStream()) {
        if (static_cast<SynapseDefinition*>(s->getType())->isIncomingLinkingCandidate(getBindingSignals().keySet())) {
            linkIncoming(s, excludedInputAct);
        }
    }
}

void ConjunctiveActivation::linkIncoming(Synapse* targetSyn, Activation* excludedInputAct) {
    for (auto& iAct : collectLinkingTargets(targetSyn->getInput(getModel()))) {
        if (iAct != excludedInputAct) {
            targetSyn->createLink(iAct, this);
        }
    }
}

void ConjunctiveActivation::addInputLink(Link* l) {
    Synapse* syn = l->getSynapse();
    assert(inputLinks.find(syn->getSynapseId()) == inputLinks.end());
    inputLinks[syn->getSynapseId()] = l;
}

std::vector<Link*> ConjunctiveActivation::getInputLinks() {
    std::vector<Link*> result;
    for (const auto& pair : inputLinks) {
        result.push_back(pair.second);
    }
    return result;
} 