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
class SynapseType;

/**
 * Configuration structure for pairing relationships
 */
struct PairingConfig {
    SynapseType* pairedSynapseType;
    int bindingSignalSlot;   // Optional binding signal slot (-1 if not used)
    
    // Constructor for no pairing
    PairingConfig() : pairedSynapseType(nullptr), bindingSignalSlot(-1) {}
    
    // Constructor for simple pairing (no binding signal slot)
    PairingConfig(SynapseType* paired) : pairedSynapseType(paired), bindingSignalSlot(-1) {}
    
    // Constructor for pairing with binding signal slot
    PairingConfig(SynapseType* paired, int slot) 
        : pairedSynapseType(paired), bindingSignalSlot(slot) {}
};


#include <set>
#include <vector>

class SynapseType : public Type {
public:

    static const RelationSelf SELF;
    static const RelationOne INPUT;
    static const RelationOne OUTPUT;
    static const RelationMany LINK;

    SynapseType(TypeRegistry* registry, const std::string& name);

    std::vector<Relation*> getRelations() const;

    Synapse* instantiate();
    Synapse* instantiate(Neuron* input, Neuron* output);

    NeuronType* getInputType() const;
    void setInputType(NeuronType* inputType);

    NeuronType* getOutputType() const;
    void setOutputType(NeuronType* outputType);

    LinkType* getLinkType() const;
    void setLinkType(LinkType* link);

    int mapTransitionForward(int bsSlot) const;
    int mapTransitionBackward(int bsSlot) const;

    std::vector<Transition*> getTransitions() const;
    void setTransitions(const std::vector<Transition*>& transitions);

    NetworkDirection* getStoredAt() const;
    void setStoredAt(NetworkDirection* storedAt);

    SynapseType* getInstanceSynapseType() const;
    void setInstanceSynapseType(SynapseType* instanceSynapseType);

    // New dual pairing configuration methods
    const PairingConfig& getInputSidePairingConfig() const;
    void setInputSidePairingConfig(const PairingConfig& config);
    
    const PairingConfig& getOutputSidePairingConfig() const;
    void setOutputSidePairingConfig(const PairingConfig& config);
    
    // Legacy methods (for backward compatibility) - now return first available pairing
    const PairingConfig& getPairingConfig() const;
    void setPairingConfig(const PairingConfig& config);
    SynapseType* getPairedSynapseType() const;
    void setPairedSynapseType(SynapseType* pairedSynapseType);

    // Derived fields from PairingConfigs
    bool getAllowLatentLinking() const;
    void setAllowLatentLinking(bool allowLatentLinking);

    std::string toString() const;

private:
    SynapseType* instanceSynapseType;

    // Dual pairing configurations - separate for input and output sides
    PairingConfig inputSidePairingConfig;
    PairingConfig outputSidePairingConfig;
    
    // Derived fields computed from PairingConfigs
    bool allowLatentLinking;

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