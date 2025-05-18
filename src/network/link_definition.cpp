#include "network/link_definition.h"

const RelationSelf LinkDefinition::SELF = RelationSelf(0, "SELF");
const RelationOne LinkDefinition::INPUT = RelationOne(1, "INPUT");
const RelationOne LinkDefinition::OUTPUT = RelationOne(2, "OUTPUT");
const RelationOne LinkDefinition::SYNAPSE = RelationOne(3, "SYNAPSE");
const RelationOne LinkDefinition::CORRESPONDING_INPUT_LINK = RelationOne(4, "CORRESPONDING_INPUT_LINK");
const RelationOne LinkDefinition::CORRESPONDING_OUTPUT_LINK = RelationOne(5, "CORRESPONDING_OUTPUT_LINK");

// Can't use an abstract class Relation in a vector directly
// Will need to implement a different approach
// Commenting out for now to fix compilation
// const std::vector<Relation> LinkDefinition::RELATIONS = {SELF, INPUT, OUTPUT, SYNAPSE, CORRESPONDING_INPUT_LINK, CORRESPONDING_OUTPUT_LINK};

LinkDefinition::LinkDefinition(TypeRegistry* registry, const std::string& name) : Type(registry, name) {}

std::vector<Relation*> LinkDefinition::getRelations() const {
    // Return a vector of pointers to avoid the abstract class issue
    return {
        const_cast<RelationSelf*>(&SELF),
        const_cast<RelationOne*>(&INPUT),
        const_cast<RelationOne*>(&OUTPUT),
        const_cast<RelationOne*>(&SYNAPSE),
        const_cast<RelationOne*>(&CORRESPONDING_INPUT_LINK),
        const_cast<RelationOne*>(&CORRESPONDING_OUTPUT_LINK)
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

ActivationDefinition* LinkDefinition::getInput() const {
    return input;
}

LinkDefinition* LinkDefinition::setInput(ActivationDefinition* input) {
    this->input = input;
    return this;
}

ActivationDefinition* LinkDefinition::getOutput() const {
    return output;
}

LinkDefinition* LinkDefinition::setOutput(ActivationDefinition* output) {
    this->output = output;
    return this;
}

std::string LinkDefinition::toString() const {
    return "LinkDefinition: " + name;
} 