#include "network/synapse_definition.h"

const RelationSelf SynapseDefinition::SELF = RelationSelf(0, "SELF");
const RelationOne SynapseDefinition::INPUT = RelationOne(1, "INPUT");
const RelationOne SynapseDefinition::OUTPUT = RelationOne(2, "OUTPUT");
const RelationMany SynapseDefinition::LINK = RelationMany(3, "LINK");
// Cannot store abstract class Relation in vector, need to use pointers instead
// const std::vector<Relation> SynapseDefinition::RELATIONS = {SELF, INPUT, OUTPUT, LINK};

// Static initializer to set up reverse relationships
class SynapseDefinitionInitializer {
public:
    SynapseDefinitionInitializer() {
        // Set up bidirectional relationships
        const_cast<RelationOne&>(SynapseDefinition::INPUT).setReversed(const_cast<RelationOne*>(&SynapseDefinition::OUTPUT));
        const_cast<RelationOne&>(SynapseDefinition::OUTPUT).setReversed(const_cast<RelationOne*>(&SynapseDefinition::INPUT));
        
        // SELF and LINK are their own reverse
        const_cast<RelationSelf&>(SynapseDefinition::SELF).setReversed(const_cast<RelationSelf*>(&SynapseDefinition::SELF));
        const_cast<RelationMany&>(SynapseDefinition::LINK).setReversed(const_cast<RelationMany*>(&SynapseDefinition::LINK));
    }
};

static SynapseDefinitionInitializer synapseDefInit;

SynapseDefinition::SynapseDefinition(TypeRegistry* registry, const std::string& name) : Type(registry, name), subType(SynapseSubType::CONJUNCTIVE), link(nullptr), input(nullptr), output(nullptr), storedAt(nullptr), trainingAllowed(false), instanceSynapseType(nullptr) {}

std::vector<Relation> SynapseDefinition::getRelations() const {
    // We can't return a vector of the abstract class Relation
    // For now, return an empty vector
    return std::vector<Relation>();
}

Synapse* SynapseDefinition::instantiate() {
    // Implementation for instantiating a Synapse
    return nullptr; // Placeholder
}

Synapse* SynapseDefinition::instantiate(Neuron* input, Neuron* output) {
    // Implementation for instantiating a Synapse with input and output
    return nullptr; // Placeholder
}

SynapseDefinition::SynapseSubType SynapseDefinition::getSubType() const {
    return subType;
}

SynapseDefinition* SynapseDefinition::setSubType(SynapseSubType subType) {
    this->subType = subType;
    return this;
}

NeuronType* SynapseDefinition::getInput() const {
    return input;
}

SynapseDefinition* SynapseDefinition::setInput(NeuronType* input) {
    this->input = input;
    return this;
}

NeuronType* SynapseDefinition::getOutput() const {
    return output;
}

SynapseDefinition* SynapseDefinition::setOutput(NeuronType* outputDef) {
    this->output = outputDef;
    return this;
}

LinkDefinition* SynapseDefinition::getLink() const {
    return link;
}

SynapseDefinition* SynapseDefinition::setLink(LinkDefinition* link) {
    this->link = link;
    return this;
}

bool SynapseDefinition::isIncomingLinkingCandidate(const std::set<BSType*>& BSTypes) const {
    // Implementation for checking incoming linking candidate
    return false; // Placeholder
}

bool SynapseDefinition::isOutgoingLinkingCandidate(const std::set<BSType*>& BSTypes) const {
    // Implementation for checking outgoing linking candidate
    return false; // Placeholder
}

BSType* SynapseDefinition::mapTransitionForward(BSType* bsType) const {
    // Implementation for mapping transition forward
    return nullptr; // Placeholder
}

BSType* SynapseDefinition::mapTransitionBackward(BSType* bsType) const {
    // Implementation for mapping transition backward
    return nullptr; // Placeholder
}

std::vector<Transition*> SynapseDefinition::getTransition() const {
    return transition;
}

SynapseDefinition* SynapseDefinition::setTransition(const std::vector<Transition*>& transition) {
    this->transition = transition;
    return this;
}

NetworkDirection* SynapseDefinition::getStoredAt() const {
    return storedAt;
}

SynapseDefinition* SynapseDefinition::setStoredAt(NetworkDirection* storedAt) {
    this->storedAt = storedAt;
    return this;
}

SynapseDefinition* SynapseDefinition::setTrainingAllowed(bool trainingAllowed) {
    this->trainingAllowed = trainingAllowed;
    return this;
}

SynapseDefinition* SynapseDefinition::getInstanceSynapseType() const {
    return instanceSynapseType;
}

SynapseDefinition* SynapseDefinition::setInstanceSynapseType(SynapseDefinition* instanceSynapseType) {
    this->instanceSynapseType = instanceSynapseType;
    return this;
}

std::string SynapseDefinition::toString() const {
    return "SynapseDefinition: " + name;
} 