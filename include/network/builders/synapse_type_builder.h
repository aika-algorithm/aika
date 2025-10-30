#ifndef AIKA_SYNAPSE_TYPE_BUILDER_H
#define AIKA_SYNAPSE_TYPE_BUILDER_H

#include "fields/type.h"
#include "fields/relation.h"
#include "network/types/synapse_type.h"
#include "network/transition.h"
#include <string>
#include <vector>

// Forward declarations
class TypeRegistry;
class SynapseType;
enum class PairingType;
struct PairingConfig;

/**
 * Builder for configuring SynapseType instances.
 * Separates configuration concerns from runtime implementation.
 */
class SynapseTypeBuilder {
public:
    explicit SynapseTypeBuilder(TypeRegistry* registry, const std::string& name);
    ~SynapseTypeBuilder();

    std::string getName() const;
    TypeRegistry* getTypeRegistry() const;

    // Builder configuration methods
    SynapseTypeBuilder& setInput(NeuronType* inputType);
    NeuronType* getInput() const;

    SynapseTypeBuilder& setOutput(NeuronType* outputType);
    NeuronType* getOutput() const;

    // Pairing methods - side bits determine storage location
    SynapseTypeBuilder& pairBySynapse(SynapseType* pairedSynapseType);  // Default output-side
    SynapseTypeBuilder& pairByBindingSignal(SynapseType* pairedSynapseType,
                                          bool thisInputSide, 
                                          bool pairedInputSide, 
                                          int bindingSignalSlot);
    
    // Legacy method (for backward compatibility)
    SynapseTypeBuilder& setPairedSynapseType(SynapseType* pairedSynapseType);
    
    // Getters for dual pairing configuration
    const PairingConfig& getInputSidePairingConfig() const;
    const PairingConfig& getOutputSidePairingConfig() const;
    
    // Legacy getters (backward compatibility) - return first available pairing
    const PairingConfig& getPairingConfig() const;
    SynapseType* getPairedSynapseType() const;

    SynapseTypeBuilder& addTransition(Transition* transition);
    std::vector<Transition*> getTransitions() const;

    SynapseTypeBuilder& addParent(SynapseType* parentType);
    std::vector<SynapseType*> getParents() const;

    // Build the actual implementation
    SynapseType* build();

private:
    std::string name;
    TypeRegistry* registry;
    NeuronType* inputType;
    NeuronType* outputType;
    LinkType* linkType;
    PairingConfig inputSidePairingConfig;
    PairingConfig outputSidePairingConfig;
    // Track side information for bidirectional setup
    bool inputSideThisInputSide;
    bool inputSidePairedInputSide;
    bool outputSideThisInputSide;
    bool outputSidePairedInputSide;
    std::vector<Transition*> transitions;
    std::vector<SynapseType*> parentTypes;
    SynapseType* builtInstance;
    bool isBuilt;
};

#endif //AIKA_SYNAPSE_TYPE_BUILDER_H