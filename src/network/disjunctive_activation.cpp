#include "network/disjunctive_activation.h"
#include "fields/relation.h"
#include "fields/rel_obj_iterator.h"
#include "network/link.h"
#include <cassert>

DisjunctiveActivation::DisjunctiveActivation(ActivationType* t, Activation* parent, int id, Neuron* n, Document* doc, std::map<int, BindingSignal*> bindingSignals)
    : Activation(t, parent, id, n, doc, bindingSignals) {}

DisjunctiveActivation::~DisjunctiveActivation() {}

RelatedObjectIterable* DisjunctiveActivation::followManyRelation(Relation* rel) const {
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

void DisjunctiveActivation::linkIncoming(Activation* excludedInputAct) {
    // Implementation for linking incoming activations
}

void DisjunctiveActivation::addInputLink(Link* l) {
    Activation* iAct = l->getInput();
    assert(inputLinks.find(iAct->getId()) == inputLinks.end());
    inputLinks[iAct->getId()] = l;
}

std::vector<Link*> DisjunctiveActivation::getInputLinks() const {
    std::vector<Link*> result;
    for (const auto& pair : inputLinks) {
        result.push_back(pair.second);
    }
    return result;
} 