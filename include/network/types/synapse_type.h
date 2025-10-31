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
 * Enumeration for different pairing types between synapses
 */
enum class PairingType {
    NONE,           // No pairing
    BY_SYNAPSE,     // Simple synapse-to-synapse pairing (output-side default)
    BY_BINDING_SIGNAL // Binding signal based pairing with configurable sides
};

/**
 * Configuration structure for pairing relationships
 */
struct PairingConfig {
    PairingType type;
    SynapseType* pairedSynapseType;
    int bindingSignalSlot;   // For BY_BINDING_SIGNAL: which binding signal slot to use
    
    // Constructor for no pairing
    PairingConfig() : type(PairingType::NONE), pairedSynapseType(nullptr), bindingSignalSlot(0) {}
    
    // Constructor for BY_SYNAPSE pairing
    PairingConfig(SynapseType* paired) : type(PairingType::BY_SYNAPSE), pairedSynapseType(paired), bindingSignalSlot(0) {}
    
    // Constructor for BY_BINDING_SIGNAL pairing
    PairingConfig(SynapseType* paired, int slot) 
        : type(PairingType::BY_BINDING_SIGNAL), pairedSynapseType(paired), bindingSignalSlot(slot) {}
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

    std::string toString() const;

private:
    SynapseType* instanceSynapseType;

    // Dual pairing configurations - separate for input and output sides
    PairingConfig inputSidePairingConfig;
    PairingConfig outputSidePairingConfig;

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