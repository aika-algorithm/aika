#include "network/synapse_type.h"

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

SynapseType::SynapseType(TypeRegistry* registry, const std::string& name) : Type(registry, name), subType(SynapseSubType::CONJUNCTIVE), link(nullptr), input(nullptr), output(nullptr), storedAt(nullptr), trainingAllowed(false), instanceSynapseType(nullptr) {}

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

SynapseType::SynapseSubType SynapseType::getSubType() const {
    return subType;
}

SynapseType* SynapseType::setSubType(SynapseSubType subType) {
    this->subType = subType;
    return this;
}

NeuronType* SynapseType::getInput() const {
    return input;
}

SynapseType* SynapseType::setInput(NeuronType* input) {
    this->input = input;
    return this;
}

NeuronType* SynapseType::getOutput() const {
    return output;
}

SynapseType* SynapseType::setOutput(NeuronType* outputDef) {
    this->output = outputDef;
    return this;
}

LinkType* SynapseType::getLink() const {
    return link;
}

SynapseType* SynapseType::setLink(LinkType* link) {
    this->link = link;
    return this;
}

bool SynapseType::isIncomingLinkingCandidate(const std::set<int>& BSTypes) const {
    // Implementation for checking incoming linking candidate
    return false; // Placeholder
}

bool SynapseType::isOutgoingLinkingCandidate(const std::set<int>& BSTypes) const {
    // Implementation for checking outgoing linking candidate
    return false; // Placeholder
}

int SynapseType::mapTransitionForward(int bsType) const {
    // Implementation for mapping transition forward
    return nullptr; // Placeholder
}

int SynapseType::mapTransitionBackward(int bsType) const {
    // Implementation for mapping transition backward
    return nullptr; // Placeholder
}

std::vector<Transition*> SynapseType::getTransitions() const {
    return transitions;
}

SynapseType* SynapseType::setTransitions(const std::vector<Transition*>& transitions) {
    this->transitions = transitions;
    return this;
}

NetworkDirection* SynapseType::getStoredAt() const {
    return storedAt;
}

SynapseType* SynapseType::setStoredAt(NetworkDirection* storedAt) {
    this->storedAt = storedAt;
    return this;
}

SynapseType* SynapseType::getInstanceSynapseType() const {
    return instanceSynapseType;
}

SynapseType* SynapseType::setInstanceSynapseType(SynapseType* instanceSynapseType) {
    this->instanceSynapseType = instanceSynapseType;
    return this;
}

std::string SynapseType::toString() const {
    return "SynapseType: " + name;
} 