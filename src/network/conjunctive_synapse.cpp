#include "network/conjunctive_synapse.h"

ConjunctiveSynapse::ConjunctiveSynapse(SynapseDefinition* type) : Synapse(type), propagable(false) {}

ConjunctiveSynapse::ConjunctiveSynapse(SynapseDefinition* type, Neuron* input, Neuron* output) : Synapse(type, input, output), propagable(false) {}

void ConjunctiveSynapse::write(DataOutput* out) {
    Synapse::write(out);
    out->writeBoolean(propagable);
}

void ConjunctiveSynapse::readFields(DataInput* in, TypeRegistry* tr) {
    Synapse::readFields(in, tr);
    propagable = in->readBoolean();
} 