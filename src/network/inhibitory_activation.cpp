#include "network/inhibitory_activation.h"
#include "network/activation_definition.h"
#include "network/synapse_definition.h"
#include "network/binding_signal.h"

InhibitoryActivation::InhibitoryActivation(ActivationDefinition* t, Activation* parent, int id, Neuron* n, Document* doc, std::map<BSType*, BindingSignal*> bindingSignals)
    : Activation(t, parent, id, n, doc, bindingSignals) {}

void InhibitoryActivation::addInputLink(Link* l) {
    int bsId = getInputKey(l);
    assert(inputLinks.find(bsId) == inputLinks.end());
    inputLinks[bsId] = l;
}

Link* InhibitoryActivation::getInputLink(int bsId) {
    return inputLinks[bsId];
}

int InhibitoryActivation::getInputKey(Link* l) {
    BSType* wildcard = static_cast<ActivationDefinition*>(type)->getWildcard();
    BSType* inputBSType = static_cast<SynapseDefinition*>(l->getSynapse()->getType())->mapTransitionBackward(wildcard);
    BindingSignal* inputBS = l->getInput()->getBindingSignal(inputBSType);
    return inputBS->getTokenId();
}

void InhibitoryActivation::addOutputLink(Link* l) {
    int bsId = getOutputKey(l);
    assert(outputLinks.find(bsId) == outputLinks.end());
    outputLinks[bsId] = l;
}

Link* InhibitoryActivation::getOutputLink(int bsId) {
    return outputLinks[bsId];
}

int InhibitoryActivation::getOutputKey(Link* l) {
    BSType* wildcard = static_cast<ActivationDefinition*>(type)->getWildcard();
    BSType* outputBSType = static_cast<SynapseDefinition*>(l->getSynapse()->getType())->mapTransitionForward(wildcard);
    BindingSignal* outputBS = l->getOutput()->getBindingSignal(outputBSType);
    return outputBS->getTokenId();
}

void InhibitoryActivation::linkIncoming(Activation* excludedInputAct) {}

std::vector<Link*> InhibitoryActivation::getInputLinks() {
    std::vector<Link*> result;
    for (const auto& pair : inputLinks) {
        result.push_back(pair.second);
    }
    return result;
}

std::vector<Link*> InhibitoryActivation::getOutputLinks() {
    std::vector<Link*> result;
    for (const auto& pair : outputLinks) {
        result.push_back(pair.second);
    }
    return result;
}

Link* InhibitoryActivation::getCorrespondingInputLink(Link* l) {
    int bsId = getOutputKey(l);
    return getInputLink(bsId);
}

Link* InhibitoryActivation::getCorrespondingOutputLink(Link* l) {
    int bsId = getInputKey(l);
    return getOutputLink(bsId);
} 