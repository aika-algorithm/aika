#include "network/link_type.h"

const RelationSelf LinkType::SELF = RelationSelf(0, "SELF");
const RelationOne LinkType::INPUT = RelationOne(1, "INPUT");
const RelationOne LinkType::OUTPUT = RelationOne(2, "OUTPUT");
const RelationOne LinkType::SYNAPSE = RelationOne(3, "SYNAPSE");
const RelationOne LinkType::PAIR_IN = RelationOne(4, "PAIR_IN");
const RelationOne LinkType::PAIR_OUT = RelationOne(5, "PAIR_OUT");

// Static initializer to set up reverse relationships
class LinkTypeInitializer {
public:
    LinkTypeInitializer() {
        // Set up bidirectional relationships
        const_cast<RelationOne&>(LinkType::INPUT).setReversed(const_cast<RelationOne*>(&LinkType::OUTPUT));
        const_cast<RelationOne&>(LinkType::OUTPUT).setReversed(const_cast<RelationOne*>(&LinkType::INPUT));
        const_cast<RelationOne&>(LinkType::PAIR_IN).setReversed(const_cast<RelationOne*>(&LinkType::PAIR_OUT));
        const_cast<RelationOne&>(LinkType::PAIR_OUT).setReversed(const_cast<RelationOne*>(&LinkType::PAIR_IN));
        
        // SYNAPSE and SELF are their own reverse
        const_cast<RelationOne&>(LinkType::SYNAPSE).setReversed(const_cast<RelationOne*>(&LinkType::SYNAPSE));
        const_cast<RelationSelf&>(LinkType::SELF).setReversed(const_cast<RelationSelf*>(&LinkType::SELF));
    }
};

static LinkTypeInitializer linkDefInit;

// Can't use an abstract class Relation in a vector directly
// Will need to implement a different approach
// Commenting out for now to fix compilation
// const std::vector<Relation> LinkType::RELATIONS = {SELF, INPUT, OUTPUT, SYNAPSE, PAIR_IN, PAIR_OUT};

LinkType::LinkType(TypeRegistry* registry, const std::string& name) : Type(registry, name) {}

std::vector<Relation*> LinkType::getRelations() const {
    // Return a vector of pointers to avoid the abstract class issue
    return {
        const_cast<RelationSelf*>(&SELF),
        const_cast<RelationOne*>(&INPUT),
        const_cast<RelationOne*>(&OUTPUT),
        const_cast<RelationOne*>(&SYNAPSE),
        const_cast<RelationOne*>(&PAIR_IN),
        const_cast<RelationOne*>(&PAIR_OUT)
    };
}

Link* LinkType::instantiate(Synapse* synapse, Activation* input, Activation* output) {
    // Implementation for instantiating a Link
    return nullptr; // Placeholder
}

SynapseType* LinkType::getSynapseType() const {
    return synapseType;
}

LinkType* LinkType::setSynapseType(SynapseType* synapseType) {
    this->synapseType = synapseType;
    return this;
}

ActivationType* LinkType::getInputType() const {
    return inputType;
}

LinkType* LinkType::setInputType(ActivationType* inputType) {
    this->inputType = inputType;
    return this;
}

ActivationType* LinkType::getOutputType() const {
    return outputType;
}

LinkType* LinkType::setOutputType(ActivationType* outputType) {
    this->outputType = outputType;
    return this;
}

std::string LinkType::toString() const {
    return "LinkType: " + name;
} 