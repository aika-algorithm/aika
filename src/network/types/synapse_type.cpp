#include "network/types/synapse_type.h"
#include "network/synapse.h"

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

SynapseType* SynapseType::getPairedSynapseType() const {
    return pairedSynapseType;
}

void SynapseType::setPairedSynapseType(SynapseType* pairedSynapseType) {
    this->pairedSynapseType = pairedSynapseType;
}

std::string SynapseType::toString() const {
    return "SynapseType: " + name;
} 