#include "network/activation.h"
#include "network/direction.h"
#include "network/input.h"
#include "network/output.h"
#include "network/activation_definition.h"
#include "fields/rel_obj_iterator.h"
#include "fields/field.h"
#include "network/document.h"
#include "network/fired.h"
#include "network/link.h"
#include "network/binding_signal.h"
#include <cassert>
#include <stdexcept>

const std::function<bool(Activation*, Activation*)> Activation::ID_COMPARATOR = [](Activation* a1, Activation* a2) {
    return a1->getId() < a2->getId();
};

Activation::Activation(ActivationDefinition* t, Activation* parent, int id, Neuron* n, Document* doc, std::map<BSType*, BindingSignal*> bindingSignals)
    : Object(t), id(id), neuron(n), doc(doc), bindingSignals(bindingSignals), parent(parent), created(-1), fired(-1), firedStep(new Fired(this)) {
    doc->addActivation(this);
    neuron->updateLastUsed(doc->getId());
    setCreated(doc->getCurrentTimestamp());
}

Activation::~Activation() {
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

void Activation::addOutputLink(Link* l) {
    Activation* oAct = l->getOutput();
    assert(outputLinks.find(oAct->getId()) == outputLinks.end());
    outputLinks[oAct->getId()] = l;
}

BindingSignal* Activation::getBindingSignal(BSType* s) const {
    auto it = bindingSignals.find(s);
    if (it != bindingSignals.end()) {
        return it->second;
    }
    return nullptr;
}

std::map<BSType*, BindingSignal*> Activation::getBindingSignals() const {
    return bindingSignals;
}

bool Activation::hasConflictingBindingSignals(std::map<BSType*, BindingSignal*> targetBindingSignals) const {
    for (const auto& e : targetBindingSignals) {
        if (isConflictingBindingSignal(e.first, e.second)) {
            return true;
        }
    }
    return false;
}

bool Activation::isConflictingBindingSignal(BSType* s, BindingSignal* targetBS) const {
    auto it = bindingSignals.find(s);
    BindingSignal* bs = (it != bindingSignals.end()) ? it->second : nullptr;
    return bs != nullptr && targetBS != bs;
}

bool Activation::hasNewBindingSignals(std::map<BSType*, BindingSignal*> targetBindingSignals) const {
    for (const auto& e : targetBindingSignals) {
        if (bindingSignals.find(e.first) == bindingSignals.end()) {
            return true;
        }
    }
    return false;
}

Activation* Activation::branch(std::map<BSType*, BindingSignal*> bindingSignals) {
    // TODO: Check: Is it necessary to remove the parents binding-signals beforehand?
    std::map<BSType*, BindingSignal*> newBindingSignals = bindingSignals;
    for (const auto& bs : getBindingSignals()) {
        newBindingSignals.erase(bs.first);
    }

    return neuron->createActivation(this, getDocument(), newBindingSignals);
}

void Activation::linkOutgoing() {
    neuron->wakeupPropagable();

    for (auto& s : neuron->getOutputSynapsesAsStream()) {
        std::set<BSType*> bindingSignalKeys;
        for (const auto& bs : getBindingSignals()) {
            bindingSignalKeys.insert(bs.first);
        }
        if (static_cast<SynapseDefinition*>(s->getType())->isOutgoingLinkingCandidate(bindingSignalKeys)) {
            linkOutgoing(s);
        }
    }
}

void Activation::linkOutgoing(Synapse* targetSyn) {
    std::set<Activation*> targets = collectLinkingTargets(targetSyn->getOutput(getModel()));

    for (auto& targetAct : targets) {
        targetSyn->createLink(this, targetAct);
    }

    if (targets.empty() && targetSyn->isPropagable()) {
        propagate(targetSyn);
    }
}

void Activation::propagate(Synapse* targetSyn) {
    std::map<BSType*, BindingSignal*> bindingSignals = targetSyn->transitionForward(getBindingSignals());
    Activation* oAct = targetSyn->getOutput(getModel())->createActivation(nullptr, getDocument(), bindingSignals);

    targetSyn->createLink(this, bindingSignals, oAct);

    oAct->linkIncoming(this);
}

std::set<Activation*> Activation::collectLinkingTargets(Neuron* n) {
    std::set<Activation*> result;
    for (auto& bs : getBindingSignals()) {
        auto activations = bs.second->getActivations(n);
        result.insert(activations.begin(), activations.end());
    }
    return result;
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
    fired = doc->getCurrentTimestamp();
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
        doc->removeStep(firedStep);
    }

    firedStep->updateNet(net->getUpdatedValue());
    doc->addStep(firedStep);
}

Queue* Activation::getQueue() const {
    return doc;
}

Neuron* Activation::getNeuron() const {
    return neuron;
}

Document* Activation::getDocument() const {
    return doc;
}

Model* Activation::getModel() const {
    return neuron->getModel();
}

Config* Activation::getConfig() const {
    return neuron->getModel()->getConfig();
}

Link* Activation::getCorrespondingInputLink(const Link* l) const {
    return nullptr;
}

Link* Activation::getCorrespondingOutputLink(const Link* l) const {
    return nullptr;
}

std::vector<Link*> Activation::getInputLinks(LinkDefinition* linkDefinition) const {
    return getInputLinks();
}

std::vector<Link*> Activation::getOutputLinks(LinkDefinition* linkDefinition) const {
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
    auto it = outputLinks.find(n->getId());
    if (it != outputLinks.end()) {
        return it->second;
    }
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