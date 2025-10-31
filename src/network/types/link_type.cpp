#include "network/types/link_type.h"
#include "network/link.h"

const RelationSelf LinkType::SELF = RelationSelf(0, "SELF");
const RelationOne LinkType::INPUT = RelationOne(1, "INPUT");
const RelationOne LinkType::OUTPUT = RelationOne(2, "OUTPUT");
const RelationOne LinkType::SYNAPSE = RelationOne(3, "SYNAPSE");
const RelationOne LinkType::PAIR = RelationOne(4, "PAIR");
const RelationOne LinkType::PAIR_IN = RelationOne(5, "PAIR_IN");
const RelationOne LinkType::PAIR_OUT = RelationOne(6, "PAIR_OUT");

// Static initializer to set up reverse relationships
class LinkTypeInitializer {
public:
    LinkTypeInitializer() {
        const_cast<RelationSelf&>(LinkType::SELF).setReversed(const_cast<RelationSelf*>(&LinkType::SELF));
        const_cast<RelationOne&>(LinkType::INPUT).setReversed(const_cast<RelationOne*>(&LinkType::OUTPUT));
        const_cast<RelationOne&>(LinkType::OUTPUT).setReversed(const_cast<RelationOne*>(&LinkType::INPUT));
        const_cast<RelationOne&>(LinkType::SYNAPSE).setReversed(const_cast<RelationMany*>(&SynapseType::LINK));
        const_cast<RelationOne&>(LinkType::PAIR).setReversed(const_cast<RelationOne*>(&LinkType::PAIR));
        const_cast<RelationOne&>(LinkType::PAIR_IN).setReversed(const_cast<RelationOne*>(&LinkType::PAIR_OUT));
        const_cast<RelationOne&>(LinkType::PAIR_OUT).setReversed(const_cast<RelationOne*>(&LinkType::PAIR_IN));
    }
};

static LinkTypeInitializer linkDefInit;


LinkType::LinkType(TypeRegistry* registry, const std::string& name) : Type(registry, name) {}

std::vector<Relation*> LinkType::getRelations() const {
    // Return a vector of pointers to avoid the abstract class issue
    return {
        const_cast<RelationSelf*>(&SELF),
        const_cast<RelationOne*>(&INPUT),
        const_cast<RelationOne*>(&OUTPUT),
        const_cast<RelationOne*>(&SYNAPSE),
        const_cast<RelationOne*>(&PAIR),
        const_cast<RelationOne*>(&PAIR_IN),
        const_cast<RelationOne*>(&PAIR_OUT)
    };
}

Link* LinkType::instantiate(Synapse* synapse, Activation* input, Activation* output) {
    Link* link = new Link(this, synapse, input, output);
    
    // Register the link with both activations
    input->addOutputLink(link);
    output->addInputLink(link);
    
    return link;
}

SynapseType* LinkType::getSynapseType() const {
    return synapseType;
}

void LinkType::setSynapseType(SynapseType* synapseType) {
    this->synapseType = synapseType;
}

ActivationType* LinkType::getInputType() const {
    return inputType;
}

void LinkType::setInputType(ActivationType* inputType) {
    this->inputType = inputType;
}

ActivationType* LinkType::getOutputType() const {
    return outputType;
}

void LinkType::setOutputType(ActivationType* outputType) {
    this->outputType = outputType;
}

std::string LinkType::toString() const {
    return "LinkType: " + name;
} 