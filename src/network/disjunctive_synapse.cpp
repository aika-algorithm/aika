#include "network/disjunctive_synapse.h"

DisjunctiveSynapse::DisjunctiveSynapse(SynapseDefinition* type) : Synapse(type), propagable(true) {}

DisjunctiveSynapse::DisjunctiveSynapse(SynapseDefinition* type, Neuron* input, Neuron* output) : Synapse(type, input, output), propagable(true) {}

void DisjunctiveSynapse::link(Model* m) {
    getInput(m)->addOutputSynapse(this);
} 