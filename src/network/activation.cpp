#include "network/activation.h"
#include "network/direction.h"
#include "network/input.h"
#include "network/output.h"
#include "network/activation_type.h"
#include "fields/rel_obj_iterator.h"
#include "fields/field.h"
#include "network/context.h"
#include "network/fired.h"
#include "network/link.h"
#include "network/binding_signal.h"
#include <cassert>
#include <stdexcept>

const std::function<bool(Activation*, Activation*)> Activation::ID_COMPARATOR = [](Activation* a1, Activation* a2) {
    return a1->getId() < a2->getId();
};

Activation::Activation(ActivationType* t, Activation* parent, int id, Neuron* n, Context* ctx, std::map<int, BindingSignal*> bindingSignals)
    : Object(t), id(id), neuron(n), ctx(ctx), bindingSignals(bindingSignals), parent(parent), created(-1), fired(-1), firedStep(new Fired(this)) {
    ctx->addActivation(this);
    neuron->updateLastUsed(ctx->getId());
    setCreated(ctx->getCurrentTimestamp());
}

Activation::~Activation() {
    std::cout << "~Activation" << std::endl;
    delete firedStep;
}

RelatedObjectIterable* Activation::followManyRelation(Relation* rel) const {
    // Create a custom iterable for each relation type
    if (rel->getRelationLabel() == "INPUT") {
        // Since getInputLinks() is pure virtual, derived classes should implement specialized behavior
        // This base implementation handles the common case for OUTPUT relations
        return nullptr;
    } else if (rel->getRelationLabel() == "OUTPUT") {
        // Convert getOutputLinks() vector to an iterable
        std::vector<Link*> links = const_cast<Activation*>(this)->getOutputLinks();
        std::vector<Object*> objs;
        for (Link* link : links) {
            objs.push_back(static_cast<Object*>(link));
        }
        return new VectorObjectIterable(objs);
    } else {
        throw std::runtime_error("Invalid Relation: " + rel->getRelationLabel());
    }
}

Object* Activation::followSingleRelation(const Relation* rel) const {
    if (rel->getRelationLabel() == "SELF") {
        return const_cast<Activation*>(this);
    } else if (rel->getRelationLabel() == "NEURON") {
        return neuron;
    } else {
        throw std::runtime_error("Invalid Relation");
    }
}

ActivationKey Activation::getKey() const {
    return ActivationKey(neuron->getId(), id);
}

Activation* Activation::getParent() const {
    return parent;
}

BindingSignal* Activation::getBindingSignal(int s) const {
    auto it = bindingSignals.find(s);
    if (it != bindingSignals.end()) {
        return it->second;
    }
    return nullptr;
}

std::map<int, BindingSignal*> Activation::getBindingSignals() const {
    return bindingSignals;
}

int Activation::getId() const {
    return id;
}

long Activation::getCreated() const {
    return created;
}

void Activation::setCreated(long ts) {
    created = ts;
}

long Activation::getFired() const {
    return fired;
}

void Activation::setFired() {
    fired = ctx->getCurrentTimestamp();
}

void Activation::setFired(long f) {
    fired = f;
}

void Activation::updateFiredStep(Field* net) {
    // TODO: Add exceedsThreshold method to Field class
    // if (!net->exceedsThreshold() || fired != -1) {
    if (fired != -1) {
        return;
    }

    if (firedStep->isQueued()) {
        ctx->removeStep(firedStep);
    }

    firedStep->updateNet(net->getUpdatedValue());
    ctx->addStep(firedStep);
}

Queue* Activation::getQueue() const {
    return ctx;
}

Neuron* Activation::getNeuron() const {
    return neuron;
}

Context* Activation::getContext() const {
    return ctx;
}

Model* Activation::getModel() const {
    return neuron->getModel();
}

Config* Activation::getConfig() const {
    return neuron->getModel()->getConfig();
}

std::vector<Link*> Activation::getInputLinks(LinkType* linkDefinition) const {
    return getInputLinks();
}

void Activation::addInputLink(Link* l) {
    //assert(inputLinks.find(syn->getSynapseId()) == inputLinks.end());
    inputLinks[getInputKey(l)] = l;
}

std::vector<Link*> Activation::getInputLinks() const {
    std::vector<Link*> result;
    for (const auto& pair : inputLinks) {
        result.push_back(pair.second);
    }
    return result;
}

std::vector<int> Activation::getInputKey(Link* l) const {
    std::vector<int> key;
    key.push_back(l->getSynapse()->getSynapseId());

    for (const auto& t : static_cast<SynapseType*>(l->getSynapse()->getType())->getTransitions()) {
        BindingSignal* bs = l->getInput()->getBindingSignal(t->from());
        key.push_back(bs->getTokenId());
    }

    return key;
}

void Activation::addOutputLink(Link* l) {
    //    assert(outputLinks.find(oAct->getId()) == outputLinks.end());
    outputLinks[getOutputKey(l)] = l;
}

std::vector<Link*> Activation::getOutputLinks(LinkType* linkDefinition) const {
    return getOutputLinks();
}

std::vector<Link*> Activation::getOutputLinks() const {
    std::vector<Link*> result;
    for (const auto& pair : outputLinks) {
        result.push_back(pair.second);
    }
    return result;
}

Link* Activation::getOutputLink(Neuron* n) const {
/*    auto it = outputLinks.find(n->getId());
    if (it != outputLinks.end()) {
        return it->second;
    }
*/
    return nullptr;
}

std::vector<Link*> Activation::getOutputLinks(Synapse* s) const {
    std::vector<Link*> result;
    for (auto& l : getOutputLinks()) {
        if (l->getSynapse() == s) {
            result.push_back(l);
        }
    }
    return result;
}

std::vector<int> Activation::getOutputKey(Link* l) const {
    std::vector<int> key;
    key.push_back(l->getSynapse()->getSynapseId());

    for (const auto& t : static_cast<SynapseType*>(l->getSynapse()->getType())->getTransitions()) {
        BindingSignal* bs = l->getInput()->getBindingSignal(t->from());
        key.push_back(bs->getTokenId());
    }

    return key;
}

int Activation::compareTo(Activation* act) const {
    // For const correctness, we remove const to use with ID_COMPARATOR
    return id < act->id ? -1 : (id > act->id ? 1 : 0);
}

bool Activation::equals(Activation* o) const {
    return this == o || (o != nullptr && id == o->id);
}

int Activation::hashCode() const {
    return std::hash<int>()(id);
}

std::string Activation::toString() const {
    return type->getName() + " " + toKeyString();
}

std::string Activation::toKeyString() const {
    return "id:" + std::to_string(getId()) + " n:[" + neuron->toKeyString() + "]";
} 