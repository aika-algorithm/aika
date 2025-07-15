#include "network/disjunctive_synapse.h"
#include "fields/relation.h"
#include "fields/rel_obj_iterator.h"

DisjunctiveSynapse::DisjunctiveSynapse(SynapseDefinition* type) : Synapse(type), propagable(true) {}

DisjunctiveSynapse::DisjunctiveSynapse(SynapseDefinition* type, Neuron* input, Neuron* output) : Synapse(type, input, output), propagable(true) {}

RelatedObjectIterable* DisjunctiveSynapse::followManyRelation(Relation* rel) const {
    // Typically synapses don't have "many" relationships
    throw std::runtime_error("Invalid Relation for DisjunctiveSynapse: " + rel->getRelationLabel());
}

Object* DisjunctiveSynapse::followSingleRelation(const Relation* rel) const {
    if (rel->getRelationLabel() == "SELF") {
        return const_cast<DisjunctiveSynapse*>(this);
    } else if (rel->getRelationLabel() == "INPUT") {
        return getInput();
    } else if (rel->getRelationLabel() == "OUTPUT") {
        return getOutput();
    } else {
        throw std::runtime_error("Invalid Relation for DisjunctiveSynapse: " + rel->getRelationLabel());
    }
}

void DisjunctiveSynapse::link(Model* m) {
    getInput(m)->addOutputSynapse(this);
} 