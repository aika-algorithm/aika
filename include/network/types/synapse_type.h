#ifndef NETWORK_SYNAPSE_DEFINITION_H
#define NETWORK_SYNAPSE_DEFINITION_H

#include "fields/type.h"
#include "fields/type_registry.h"
#include "fields/relation.h"

#include "network/activation.h"
#include "network/binding_signal.h"
#include "network/transition.h"

// Forward declarations
class Synapse;
class Neuron;
class NeuronType;
class LinkType;
class NetworkDirection;


#include <set>
#include <vector>

class SynapseType : public Type {
public:

    static const RelationSelf SELF;
    static const RelationOne INPUT;
    static const RelationOne OUTPUT;
    static const RelationMany LINK;
    // Cannot store abstract class Relation in vector, need to use pointers instead
    // static const std::vector<Relation> RELATIONS;

    SynapseType(TypeRegistry* registry, const std::string& name);

    std::vector<Relation> getRelations() const;

    Synapse* instantiate();
    Synapse* instantiate(Neuron* input, Neuron* output);

    NeuronType* getInputType() const;
    void setInputType(NeuronType* inputType);

    NeuronType* getOutputType() const;
    void setOutputType(NeuronType* outputType);

    LinkType* getLinkType() const;
    void setLinkType(LinkType* link);

    int mapTransitionForward(int bsType) const;
    int mapTransitionBackward(int bsType) const;

    std::vector<Transition*> getTransitions() const;
    void setTransitions(const std::vector<Transition*>& transitions);

    NetworkDirection* getStoredAt() const;
    void setStoredAt(NetworkDirection* storedAt);

    SynapseType* getInstanceSynapseType() const;
    void setInstanceSynapseType(SynapseType* instanceSynapseType);

    SynapseType* getPairedSynapseType() const;
    void setPairedSynapseType(SynapseType* pairedSynapseType);

    std::string toString() const;

private:
    SynapseType* instanceSynapseType;

    SynapseType* pairedSynapseType;

    LinkType* linkType;

    NeuronType* inputType;
    NeuronType* outputType;

    // Transition maps for O(1) lookup
    std::map<int, int> forwardTransitionMap;  // from -> to
    std::map<int, int> backwardTransitionMap; // to -> from

    NetworkDirection* storedAt;
    
    // Private helper method
    void initializeTransitionMaps(const std::vector<Transition*>& transitions);
};

#endif // NETWORK_SYNAPSE_DEFINITION_H 