#ifndef NETWORK_DISJUNCTIVE_SYNAPSE_H
#define NETWORK_DISJUNCTIVE_SYNAPSE_H

#include "network/synapse.h"

class DisjunctiveSynapse : public Synapse {
public:
    DisjunctiveSynapse(SynapseDefinition* type);
    DisjunctiveSynapse(SynapseDefinition* type, Neuron* input, Neuron* output);

    RelatedObjectIterable* followManyRelation(Relation* rel) const override;
    Obj* followSingleRelation(const Relation* rel) const override;

    void link(Model* m);

private:
    bool propagable;
};

#endif // NETWORK_DISJUNCTIVE_SYNAPSE_H 