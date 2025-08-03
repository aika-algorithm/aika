#include "network/conjunctive_activation.h"
#include "network/synapse.h"
#include "network/synapse_type.h"
#include "fields/relation.h"
#include "fields/rel_obj_iterator.h"
#include <cassert>

ConjunctiveActivation::ConjunctiveActivation(ActivationType* t, Activation* parent, int id, Neuron* n, Document* doc, std::map<BSType*, BindingSignal*> bindingSignals)
    : Activation(t, parent, id, n, doc, bindingSignals) {}

ConjunctiveActivation::~ConjunctiveActivation() {}

RelatedObjectIterable* ConjunctiveActivation::followManyRelation(Relation* rel) const {
    if (rel->getRelationLabel() == "INPUT") {
        // Convert inputLinks to a vector of Object*
        std::vector<Object*> objs;
        for (const auto& pair : inputLinks) {
            objs.push_back(static_cast<Object*>(pair.second));
        }
        return new VectorObjectIterable(objs);
    } else {
        // Use base class implementation for other relations
        return Activation::followManyRelation(rel);
    }
}

void ConjunctiveActivation::linkIncoming(Activation* excludedInputAct) {
    for (auto& s : neuron->getInputSynapsesAsStream()) {
        // Extract keys from the map (BSType* pointers)
        std::set<BSType*> bsKeys;
        for (const auto& pair : getBindingSignals()) {
            bsKeys.insert(pair.first);
        }
        if (static_cast<SynapseType*>(s->getType())->isIncomingLinkingCandidate(bsKeys)) {
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

std::vector<Link*> ConjunctiveActivation::getInputLinks() const {
    std::vector<Link*> result;
    for (const auto& pair : inputLinks) {
        result.push_back(pair.second);
    }
    return result;
} 