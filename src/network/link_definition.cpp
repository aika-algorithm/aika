#include "network/link_definition.h"

const RelationSelf LinkDefinition::SELF = RelationSelf();
const RelationOne LinkDefinition::INPUT = RelationOne();
const RelationOne LinkDefinition::OUTPUT = RelationOne();
const RelationOne LinkDefinition::SYNAPSE = RelationOne();
const RelationOne LinkDefinition::CORRESPONDING_INPUT_LINK = RelationOne();
const RelationOne LinkDefinition::CORRESPONDING_OUTPUT_LINK = RelationOne();
const std::vector<Relation> LinkDefinition::RELATIONS = {SELF, INPUT, OUTPUT, SYNAPSE, CORRESPONDING_INPUT_LINK, CORRESPONDING_OUTPUT_LINK};

LinkDefinition::LinkDefinition(TypeRegistry* registry, const std::string& name) : Type(registry, name) {}

std::vector<Relation> LinkDefinition::getRelations() const {
    return RELATIONS;
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