#include "network/link_definition.h"

const RelationSelf LinkDefinition::SELF = RelationSelf(0, "SELF");
const RelationOne LinkDefinition::INPUT = RelationOne(1, "INPUT");
const RelationOne LinkDefinition::OUTPUT = RelationOne(2, "OUTPUT");
const RelationOne LinkDefinition::SYNAPSE = RelationOne(3, "SYNAPSE");
const RelationOne LinkDefinition::PAIR_IN = RelationOne(4, "PAIR_IN");
const RelationOne LinkDefinition::PAIR_OUT = RelationOne(5, "PAIR_OUT");

// Static initializer to set up reverse relationships
class LinkDefinitionInitializer {
public:
    LinkDefinitionInitializer() {
        // Set up bidirectional relationships
        const_cast<RelationOne&>(LinkDefinition::INPUT).setReversed(const_cast<RelationOne*>(&LinkDefinition::OUTPUT));
        const_cast<RelationOne&>(LinkDefinition::OUTPUT).setReversed(const_cast<RelationOne*>(&LinkDefinition::INPUT));
        const_cast<RelationOne&>(LinkDefinition::PAIR_IN).setReversed(const_cast<RelationOne*>(&LinkDefinition::PAIR_OUT));
        const_cast<RelationOne&>(LinkDefinition::PAIR_OUT).setReversed(const_cast<RelationOne*>(&LinkDefinition::PAIR_IN));
        
        // SYNAPSE and SELF are their own reverse
        const_cast<RelationOne&>(LinkDefinition::SYNAPSE).setReversed(const_cast<RelationOne*>(&LinkDefinition::SYNAPSE));
        const_cast<RelationSelf&>(LinkDefinition::SELF).setReversed(const_cast<RelationSelf*>(&LinkDefinition::SELF));
    }
};

static LinkDefinitionInitializer linkDefInit;

// Can't use an abstract class Relation in a vector directly
// Will need to implement a different approach
// Commenting out for now to fix compilation
// const std::vector<Relation> LinkDefinition::RELATIONS = {SELF, INPUT, OUTPUT, SYNAPSE, PAIR_IN, PAIR_OUT};

LinkDefinition::LinkDefinition(TypeRegistry* registry, const std::string& name) : Type(registry, name) {}

std::vector<Relation*> LinkDefinition::getRelations() const {
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

Link* LinkDefinition::instantiate(Synapse* synapse, Activation* input, Activation* output) {
    // Implementation for instantiating a Link
    return nullptr; // Placeholder
}

SynapseDefinition* LinkDefinition::getSynapse() const {
    return synapse;
}

LinkDefinition* LinkDefinition::setSynapse(SynapseDefinition* synapse) {
    this->synapse = synapse;
    return this;
}

ActivationType* LinkDefinition::getInput() const {
    return input;
}

LinkDefinition* LinkDefinition::setInput(ActivationType* input) {
    this->input = input;
    return this;
}

ActivationType* LinkDefinition::getOutput() const {
    return output;
}

LinkDefinition* LinkDefinition::setOutput(ActivationType* output) {
    this->output = output;
    return this;
}

std::string LinkDefinition::toString() const {
    return "LinkDefinition: " + name;
} 