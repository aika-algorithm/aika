#include "network/types/synapse_type.h"

const RelationSelf SynapseType::SELF = RelationSelf(0, "SELF");
const RelationOne SynapseType::INPUT = RelationOne(1, "INPUT");
const RelationOne SynapseType::OUTPUT = RelationOne(2, "OUTPUT");
const RelationMany SynapseType::LINK = RelationMany(3, "LINK");
// Cannot store abstract class Relation in vector, need to use pointers instead
// const std::vector<Relation> SynapseType::RELATIONS = {SELF, INPUT, OUTPUT, LINK};

// Static initializer to set up reverse relationships
class SynapseTypeInitializer {
public:
    SynapseTypeInitializer() {
        // Set up bidirectional relationships
        const_cast<RelationOne&>(SynapseType::INPUT).setReversed(const_cast<RelationOne*>(&SynapseType::OUTPUT));
        const_cast<RelationOne&>(SynapseType::OUTPUT).setReversed(const_cast<RelationOne*>(&SynapseType::INPUT));
        
        // SELF and LINK are their own reverse
        const_cast<RelationSelf&>(SynapseType::SELF).setReversed(const_cast<RelationSelf*>(&SynapseType::SELF));
        const_cast<RelationMany&>(SynapseType::LINK).setReversed(const_cast<RelationMany*>(&SynapseType::LINK));
    }
};

static SynapseTypeInitializer synapseDefInit;

SynapseType::SynapseType(TypeRegistry* registry, const std::string& name) : Type(registry, name), linkType(nullptr), inputType(nullptr), outputType(nullptr), storedAt(nullptr), instanceSynapseType(nullptr) {}

std::vector<Relation> SynapseType::getRelations() const {
    // We can't return a vector of the abstract class Relation
    // For now, return an empty vector
    return std::vector<Relation>();
}

Synapse* SynapseType::instantiate() {
    // Implementation for instantiating a Synapse
    return nullptr; // Placeholder
}

Synapse* SynapseType::instantiate(Neuron* input, Neuron* output) {
    // Implementation for instantiating a Synapse with input and output
    return nullptr; // Placeholder
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
    // Implementation for mapping transition forward
    return -1; // Placeholder
}

int SynapseType::mapTransitionBackward(int bsType) const {
    // Implementation for mapping transition backward
    return -1; // Placeholder
}

std::vector<Transition*> SynapseType::getTransitions() const {
    return transitions;
}

void SynapseType::setTransitions(const std::vector<Transition*>& transitions) {
    this->transitions = transitions;
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

SynapseType* SynapseType::getPairedSynapseType() const {
    return pairedSynapseType;
}

void SynapseType::setPairedSynapseType(SynapseType* pairedSynapseType) {
    this->pairedSynapseType = pairedSynapseType;
}

std::string SynapseType::toString() const {
    return "SynapseType: " + name;
} 