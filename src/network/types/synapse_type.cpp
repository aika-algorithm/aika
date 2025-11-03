#include "network/types/synapse_type.h"
#include "network/synapse.h"

const RelationSelf SynapseType::SELF = RelationSelf(0, "SELF");
const RelationOne SynapseType::INPUT = RelationOne(1, "INPUT");
const RelationOne SynapseType::OUTPUT = RelationOne(2, "OUTPUT");
const RelationMany SynapseType::LINK = RelationMany(3, "LINK");
const RelationOne SynapseType::PAIR = RelationOne(4, "PAIR");
const RelationOne SynapseType::PAIR_IN = RelationOne(5, "PAIR_IN");
const RelationOne SynapseType::PAIR_OUT = RelationOne(6, "PAIR_OUT");


// Static initializer to set up reverse relationships
class SynapseTypeInitializer {
public:
    SynapseTypeInitializer() {
        const_cast<RelationSelf&>(SynapseType::SELF).setReversed(const_cast<RelationSelf*>(&SynapseType::SELF));
        const_cast<RelationOne&>(SynapseType::INPUT).setReversed(const_cast<RelationOne*>(&SynapseType::OUTPUT));
        const_cast<RelationOne&>(SynapseType::OUTPUT).setReversed(const_cast<RelationOne*>(&SynapseType::INPUT));
        const_cast<RelationMany&>(SynapseType::LINK).setReversed(const_cast<RelationOne*>(&LinkType::SYNAPSE));
        const_cast<RelationOne&>(SynapseType::PAIR).setReversed(const_cast<RelationOne*>(&SynapseType::PAIR));
        const_cast<RelationOne&>(SynapseType::PAIR_IN).setReversed(const_cast<RelationOne*>(&SynapseType::PAIR_OUT));
        const_cast<RelationOne&>(SynapseType::PAIR_OUT).setReversed(const_cast<RelationOne*>(&SynapseType::PAIR_IN));
    }
};

static SynapseTypeInitializer synapseDefInit;

SynapseType::SynapseType(TypeRegistry* registry, const std::string& name) : Type(registry, name), linkType(nullptr), inputType(nullptr), outputType(nullptr), storedAt(nullptr), instanceSynapseType(nullptr), allowLatentLinking(false) {}


std::vector<Relation*> SynapseType::getRelations() const {
    // Return a vector of pointers to avoid the abstract class issue
    return {
        const_cast<RelationSelf*>(&SELF),
        const_cast<RelationOne*>(&INPUT),
        const_cast<RelationOne*>(&OUTPUT),
        const_cast<RelationMany*>(&LINK)
    };
}

Synapse* SynapseType::instantiate() {
    return new Synapse(this);
}

Synapse* SynapseType::instantiate(Neuron* input, Neuron* output) {
    return new Synapse(this, input, output);
}

NeuronType* SynapseType::getInputType() const {
    return inputType;
}

void SynapseType::setInputType(NeuronType* inputType) {
    this->inputType = inputType;
}

NeuronType* SynapseType::getOutputType() const {
    return outputType;
}

void SynapseType::setOutputType(NeuronType* outputType) {
    this->outputType = outputType;
}

LinkType* SynapseType::getLinkType() const {
    return linkType;
}

void SynapseType::setLinkType(LinkType* linkType) {
    this->linkType = linkType;
}

int SynapseType::mapTransitionForward(int bsType) const {
    auto it = forwardTransitionMap.find(bsType);
    return (it != forwardTransitionMap.end()) ? it->second : -1;
}

int SynapseType::mapTransitionBackward(int bsType) const {
    auto it = backwardTransitionMap.find(bsType);
    return (it != backwardTransitionMap.end()) ? it->second : -1;
}

std::vector<Transition*> SynapseType::getTransitions() const {
    // Reconstruct transitions vector from maps for backward compatibility
    std::vector<Transition*> result;
    for (const auto& pair : forwardTransitionMap) {
        result.push_back(Transition::of(pair.first, pair.second));
    }
    return result;
}

void SynapseType::setTransitions(const std::vector<Transition*>& transitions) {
    initializeTransitionMaps(transitions);
}

void SynapseType::initializeTransitionMaps(const std::vector<Transition*>& transitions) {
    // Clear existing maps
    forwardTransitionMap.clear();
    backwardTransitionMap.clear();
    
    // Build maps from transitions
    for (const Transition* t : transitions) {
        if (t) {
            forwardTransitionMap[t->from()] = t->to();
            backwardTransitionMap[t->to()] = t->from();
        }
    }
}

NetworkDirection* SynapseType::getStoredAt() const {
    return storedAt;
}

void SynapseType::setStoredAt(NetworkDirection* storedAt) {
    this->storedAt = storedAt;
}

SynapseType* SynapseType::getInstanceSynapseType() const {
    return instanceSynapseType;
}

void SynapseType::setInstanceSynapseType(SynapseType* instanceSynapseType) {
    this->instanceSynapseType = instanceSynapseType;
}

// New dual pairing configuration methods
const PairingConfig& SynapseType::getInputSidePairingConfig() const {
    return inputSidePairingConfig;
}

void SynapseType::setInputSidePairingConfig(const PairingConfig& config) {
    this->inputSidePairingConfig = config;
}

const PairingConfig& SynapseType::getOutputSidePairingConfig() const {
    return outputSidePairingConfig;
}

void SynapseType::setOutputSidePairingConfig(const PairingConfig& config) {
    this->outputSidePairingConfig = config;
}

// Legacy methods (for backward compatibility) - return first available pairing
const PairingConfig& SynapseType::getPairingConfig() const {
    // Return output-side pairing if available, otherwise input-side
    if (outputSidePairingConfig.pairedSynapseType != nullptr) {
        return outputSidePairingConfig;
    }
    return inputSidePairingConfig;
}

void SynapseType::setPairingConfig(const PairingConfig& config) {
    // Default to output-side for legacy compatibility
    this->outputSidePairingConfig = config;
}

SynapseType* SynapseType::getPairedSynapseType() const {
    // Return output-side paired synapse if available, otherwise input-side
    if (outputSidePairingConfig.pairedSynapseType != nullptr) {
        return outputSidePairingConfig.pairedSynapseType;
    }
    return inputSidePairingConfig.pairedSynapseType;
}

void SynapseType::setPairedSynapseType(SynapseType* pairedSynapseType) {
    // Default to output-side BY_SYNAPSE pairing for legacy compatibility
    this->outputSidePairingConfig = PairingConfig(pairedSynapseType);
}

bool SynapseType::getAllowLatentLinking() const {
    return allowLatentLinking;
}

void SynapseType::setAllowLatentLinking(bool allowLatentLinking) {
    this->allowLatentLinking = allowLatentLinking;
}

std::string SynapseType::toString() const {
    return "SynapseType: " + name;
} 