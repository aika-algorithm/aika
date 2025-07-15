#include "network/link.h"
#include "network/model.h"
#include "network/document.h"
#include "network/element.h"
#include "network/model_provider.h"
#include "network/link_definition.h"
#include "network/synapse.h"
#include "fields/object.h"
#include "fields/queue.h"
#include "fields/queue_provider.h"
#include "network/timestamp.h"

Link::Link(LinkDefinition* type, Synapse* s, Activation* input, Activation* output)
    : Object(type), synapse(s), input(input), output(output) {
    // initFields() call removed as it doesn't exist in Object class
    input->addOutputLink(this);
    output->addInputLink(this);
}

RelatedObjectIterable* Link::followManyRelation(Relation* rel) const {
    throw std::runtime_error("Invalid Relation: " + rel->getRelationLabel());
}

Object* Link::followSingleRelation(const Relation* rel) const {
    if (rel->getRelationLabel() == "SELF") return const_cast<Link*>(this);
    if (rel->getRelationLabel() == "INPUT") return input;
    if (rel->getRelationLabel() == "OUTPUT") return output;
    if (rel->getRelationLabel() == "SYNAPSE") return synapse;
    if (rel->getRelationLabel() == "CORRESPONDING_INPUT_LINK") return input->getCorrespondingInputLink(this);
    if (rel->getRelationLabel() == "CORRESPONDING_OUTPUT_LINK") return output->getCorrespondingOutputLink(this);
    throw std::runtime_error("Invalid Relation: " + rel->getRelationLabel());
}

long Link::getFired() const {
    return input && isCausal() ? input->getFired() : output->getFired();
}

long Link::getCreated() const {
    return input && isCausal() ? input->getCreated() : output->getCreated();
}

Synapse* Link::getSynapse() const {
    return synapse;
}

void Link::setSynapse(Synapse* synapse) {
    this->synapse = synapse;
}

Activation* Link::getInput() const {
    return input;
}

Activation* Link::getOutput() const {
    return output;
}

bool Link::isCausal() const {
    return !input || isCausal(input, output);
}

bool Link::isCausal(Activation* iAct, Activation* oAct) {
    return iAct->getFired() < oAct->getFired();
}

Document* Link::getDocument() const {
    return output->getDocument();
}

Queue* Link::getQueue() const {
    return output->getDocument();
}

Model* Link::getModel() const {
    return output->getModel();
}

Config* Link::getConfig() const {
    return output->getModel()->getConfig();
}

std::string Link::getInputKeyString() const {
    if (input) {
        return input->toKeyString();
    } else {
        std::string neuronStr = synapse->getInput() ? synapse->getInput()->toString() : "null";
        return "id:X n:[" + neuronStr + "]";
    }
}

std::string Link::getOutputKeyString() const {
    if (output) {
        return output->toKeyString();
    } else {
        std::string neuronStr = synapse->getOutput() ? synapse->getOutput()->toString() : "null";
        return "id:X n:[" + neuronStr + "]";
    }
}

std::string Link::toString() const {
    return type->getName() + " in:[" + getInputKeyString() + "] ==> out:[" + getOutputKeyString() + "]";
}

std::string Link::toKeyString() const {
    return getInputKeyString() + " ==> " + getOutputKeyString();
} 