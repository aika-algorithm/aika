#include "network/conjunctive_synapse.h"
#include "fields/relation.h"
#include "fields/rel_obj_iterator.h"

ConjunctiveSynapse::ConjunctiveSynapse(SynapseDefinition* type) : Synapse(type), propagable(false) {}

ConjunctiveSynapse::ConjunctiveSynapse(SynapseDefinition* type, Neuron* input, Neuron* output) : Synapse(type, input, output), propagable(false) {}

RelatedObjectIterable* ConjunctiveSynapse::followManyRelation(Relation* rel) const {
    // Typically synapses don't have "many" relationships
    throw std::runtime_error("Invalid Relation for ConjunctiveSynapse: " + rel->getRelationName());
}

Obj* ConjunctiveSynapse::followSingleRelation(const Relation* rel) {
    if (rel->getRelationName() == "SELF") {
        return this;
    } else if (rel->getRelationName() == "INPUT") {
        return getInput();
    } else if (rel->getRelationName() == "OUTPUT") {
        return getOutput();
    } else {
        throw std::runtime_error("Invalid Relation for ConjunctiveSynapse: " + rel->getRelationName());
    }
}

void ConjunctiveSynapse::write(DataOutput* out) {
    Synapse::write(out);
    out->writeBoolean(propagable);
}

void ConjunctiveSynapse::readFields(DataInput* in, TypeRegistry* tr) {
    Synapse::readFields(in, tr);
    propagable = in->readBoolean();
} 