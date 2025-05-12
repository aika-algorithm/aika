#include "network/disjunctive_synapse.h"
#include "fields/relation.h"
#include "fields/rel_obj_iterator.h"

DisjunctiveSynapse::DisjunctiveSynapse(SynapseDefinition* type) : Synapse(type), propagable(true) {}

DisjunctiveSynapse::DisjunctiveSynapse(SynapseDefinition* type, Neuron* input, Neuron* output) : Synapse(type, input, output), propagable(true) {}

RelatedObjectIterable* DisjunctiveSynapse::followManyRelation(Relation* rel) const {
    // Typically synapses don't have "many" relationships
    throw std::runtime_error("Invalid Relation for DisjunctiveSynapse: " + rel->getRelationName());
}

Obj* DisjunctiveSynapse::followSingleRelation(const Relation* rel) {
    if (rel->getRelationName() == "SELF") {
        return this;
    } else if (rel->getRelationName() == "INPUT") {
        return getInput();
    } else if (rel->getRelationName() == "OUTPUT") {
        return getOutput();
    } else {
        throw std::runtime_error("Invalid Relation for DisjunctiveSynapse: " + rel->getRelationName());
    }
}

void DisjunctiveSynapse::link(Model* m) {
    getInput(m)->addOutputSynapse(this);
} 