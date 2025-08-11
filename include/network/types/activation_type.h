#ifndef NETWORK_ACTIVATION_DEFINITION_H
#define NETWORK_ACTIVATION_DEFINITION_H

#include "fields/type.h"
#include "fields/relation.h"

class NeuronType;

class ActivationType : public Type {
public:
    static const RelationSelf SELF;
    static const RelationMany INPUT;
    static const RelationMany OUTPUT;
    static const RelationOne NEURON;

    ActivationType(TypeRegistry* registry, const std::string& name);
    ~ActivationType();

    std::vector<Relation*> getRelations() const;

    NeuronType* getNeuronType() const;
    void setNeuronType(NeuronType* neuronType);

    // Add any additional methods or members specific to ActivationType here

private:
    NeuronType* neuronType;
};

#endif // NETWORK_ACTIVATION_DEFINITION_H 