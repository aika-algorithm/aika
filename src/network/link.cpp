#include "network/link.h"
#include "network/model.h"
#include "network/document.h"
#include "network/element.h"
#include "network/model_provider.h"
#include "network/link_definition.h"
#include "network/synapse.h"
#include "network/obj_impl.h"
#include "network/queue.h"
#include "network/queue_provider.h"
#include "network/timestamp.h"

Link::Link(LinkDefinition* type, Synapse* s, Activation* input, Activation* output)
    : ObjImpl(type), synapse(s), input(input), output(output) {
    initFields();
    input->addOutputLink(this);
    output->addInputLink(this);
}

RelatedObjectIterable* Link::followManyRelation(Relation* rel) const {
    throw std::runtime_error("Invalid Relation: " + rel->getRelationName());
}

Obj* Link::followSingleRelation(const Relation* rel) {
    if (rel->getRelationName() == "SELF") return this;
    if (rel->getRelationName() == "INPUT") return input;
    if (rel->getRelationName() == "OUTPUT") return output;
    if (rel->getRelationName() == "SYNAPSE") return synapse;
    if (rel->getRelationName() == "CORRESPONDING_INPUT_LINK") return input->getCorrespondingInputLink(this);
    if (rel->getRelationName() == "CORRESPONDING_OUTPUT_LINK") return output->getCorrespondingOutputLink(this);
    throw std::runtime_error("Invalid Relation: " + rel->getRelationName());
}

Timestamp Link::getFired() {
    return input && isCausal() ? input->getFired() : output->getFired();
}

Timestamp Link::getCreated() {
    return input && isCausal() ? input->getCreated() : output->getCreated();
}

Synapse* Link::getSynapse() {
    return synapse;
}

void Link::setSynapse(Synapse* synapse) {
    this->synapse = synapse;
}

Activation* Link::getInput() {
    return input;
}

Activation* Link::getOutput() {
    return output;
}

bool Link::isCausal() {
    return !input || isCausal(input, output);
}

bool Link::isCausal(Activation* iAct, Activation* oAct) {
    return iAct->getFired() < oAct->getFired();
}

Document* Link::getDocument() {
    return output->getDocument();
}

Queue* Link::getQueue() {
    return output->getDocument();
}

Model* Link::getModel() {
    return output->getModel();
}

std::string Link::getInputKeyString() {
    return input ? input->toKeyString() : "id:X n:[" + synapse->getInput() + "]";
}

std::string Link::getOutputKeyString() {
    return output ? output->toKeyString() : "id:X n:[" + synapse->getOutput() + "]";
}

std::string Link::toString() {
    return type->getName() + " in:[" + getInputKeyString() + "] ==> out:[" + getOutputKeyString() + "]";
}

std::string Link::toKeyString() {
    return getInputKeyString() + " ==> " + getOutputKeyString();
} 