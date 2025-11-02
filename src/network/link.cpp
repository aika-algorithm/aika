#include "network/link.h"
#include "network/model.h"
#include "network/context.h"
#include "network/element.h"
#include "network/model_provider.h"
#include "network/types/link_type.h"
#include "network/synapse.h"
#include "fields/object.h"
#include "fields/queue.h"
#include "fields/queue_provider.h"
#include "network/timestamp.h"

Link::Link(LinkType* type, Synapse* s, Activation* input, Activation* output)
    : Object(type), synapse(s), input(input), output(output), pairedLinkInputSide(nullptr), pairedLinkOutputSide(nullptr) {
    initFields();
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
    if (rel->getRelationLabel() == "PAIR") {
        // Return the paired input link. This pairing is attached to the output side of both links.
        return pairedLinkOutputSide;
    }
    if (rel->getRelationLabel() == "PAIR_IN") {
        // Return the paired input link. For the paired link the pair relation is attached to the output-side.
        return pairedLinkInputSide;
    }
    if (rel->getRelationLabel() == "PAIR_OUT") {
        // Return the paired output link. For the paired link the pair relation is attached to the input-side.
        return pairedLinkOutputSide;
    }
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

Context* Link::getContext() const {
    return output->getContext();
}

Queue* Link::getQueue() const {
    return output->getContext();
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

Link* Link::getPairedLinkInputSide() const {
    return pairedLinkInputSide;
}

void Link::setPairedLinkInputSide(Link* pairedLink) {
    this->pairedLinkInputSide = pairedLink;
}

Link* Link::getPairedLinkOutputSide() const {
    return pairedLinkOutputSide;
}

void Link::setPairedLinkOutputSide(Link* pairedLink) {
    this->pairedLinkOutputSide = pairedLink;
} 