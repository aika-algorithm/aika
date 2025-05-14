#include "network/inhibitory_activation.h"
#include "network/activation_definition.h"
#include "network/synapse_definition.h"
#include "network/binding_signal.h"
#include "fields/relation.h"
#include "fields/rel_obj_iterator.h"

InhibitoryActivation::InhibitoryActivation(ActivationDefinition* t, Activation* parent, int id, Neuron* n, Document* doc, std::map<BSType*, BindingSignal*> bindingSignals)
    : Activation(t, parent, id, n, doc, bindingSignals) {}
    
RelatedObjectIterable* InhibitoryActivation::followManyRelation(Relation* rel) const {
    if (rel->getRelationName() == "INPUT") {
        // Convert inputLinks to a vector of Obj*
        std::vector<Obj*> objs;
        for (const auto& pair : inputLinks) {
            objs.push_back(static_cast<Obj*>(pair.second));
        }
        return new VectorObjectIterable(objs);
    } else if (rel->getRelationName() == "OUTPUT") {
        // For InhibitoryActivation, we override both INPUT and OUTPUT handling
        std::vector<Obj*> objs;
        for (const auto& pair : outputLinks) {
            objs.push_back(static_cast<Obj*>(pair.second));
        }
        return new VectorObjectIterable(objs);
    } else {
        // Use base class implementation for other relations
        return Activation::followManyRelation(rel);
    }
}

void InhibitoryActivation::addInputLink(Link* l) {
    int bsId = getInputKey(l);
    assert(inputLinks.find(bsId) == inputLinks.end());
    inputLinks[bsId] = l;
}

Link* InhibitoryActivation::getInputLink(int bsId) const {
    auto it = inputLinks.find(bsId);
    if (it != inputLinks.end()) {
        return it->second;
    }
    return nullptr;
}

int InhibitoryActivation::getInputKey(Link* l) const {
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

Link* InhibitoryActivation::getOutputLink(int bsId) const {
    auto it = outputLinks.find(bsId);
    if (it != outputLinks.end()) {
        return it->second;
    }
    return nullptr;
}

int InhibitoryActivation::getOutputKey(Link* l) const {
    BSType* wildcard = static_cast<ActivationDefinition*>(type)->getWildcard();
    BSType* outputBSType = static_cast<SynapseDefinition*>(l->getSynapse()->getType())->mapTransitionForward(wildcard);
    BindingSignal* outputBS = l->getOutput()->getBindingSignal(outputBSType);
    return outputBS->getTokenId();
}

void InhibitoryActivation::linkIncoming(Activation* excludedInputAct) {}

std::vector<Link*> InhibitoryActivation::getInputLinks() const {
    std::vector<Link*> result;
    for (const auto& pair : inputLinks) {
        result.push_back(pair.second);
    }
    return result;
}

std::vector<Link*> InhibitoryActivation::getOutputLinks() const {
    std::vector<Link*> result;
    for (const auto& pair : outputLinks) {
        result.push_back(pair.second);
    }
    return result;
}

Link* InhibitoryActivation::getCorrespondingInputLink(const Link* l) const {
    int bsId = getOutputKey(const_cast<Link*>(l));
    return getInputLink(bsId);
}

Link* InhibitoryActivation::getCorrespondingOutputLink(const Link* l) const {
    int bsId = getInputKey(const_cast<Link*>(l));
    return getOutputLink(bsId);
} 