#ifndef NETWORK_CONJUNCTIVE_SYNAPSE_H
#define NETWORK_CONJUNCTIVE_SYNAPSE_H

#include "network/synapse.h"

class ConjunctiveSynapse : public Synapse {
public:
    ConjunctiveSynapse(SynapseDefinition* type);
    ConjunctiveSynapse(SynapseDefinition* type, Neuron* input, Neuron* output);

    RelatedObjectIterable* followManyRelation(Relation* rel) const override;
    Obj* followSingleRelation(const Relation* rel) override;
/*
    void write(DataOutput* out) override;
    void readFields(DataInput* in, TypeRegistry* tr) override;
*/
private:
    bool propagable;
};

#endif // NETWORK_CONJUNCTIVE_SYNAPSE_H 