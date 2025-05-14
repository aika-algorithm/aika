#include "network/activation.h"
#include "network/direction.h"
#include "network/input.h"
#include "network/output.h"
#include "network/activation_definition.h"

const std::function<bool(Activation*, Activation*)> Activation::ID_COMPARATOR = [](Activation* a1, Activation* a2) {
    return a1->getId() < a2->getId();
};

Activation::Activation(ActivationDefinition* t, Activation* parent, int id, Neuron* n, Document* doc, std::map<BSType, BindingSignal*> bindingSignals)
    : Obj(t), id(id), neuron(n), doc(doc), bindingSignals(bindingSignals), parent(parent), created(Timestamp::NOT_SET), fired(Timestamp::NOT_SET), firedStep(new Fired(this)) {
    doc->addActivation(this);
    neuron->updateLastUsed(doc->getId());
    setCreated(doc->getCurrentTimestamp());
}

Activation::~Activation() {
    delete firedStep;
}

RelatedObjectIterable* Activation::followManyRelation(Relation* rel) const {
    // Create a custom iterable for each relation type
    if (rel->getRelationName() == "INPUT") {
        // Since getInputLinks() is pure virtual, derived classes should implement specialized behavior
        // This base implementation handles the common case for OUTPUT relations
        return nullptr;
    } else if (rel->getRelationName() == "OUTPUT") {
        // Convert getOutputLinks() vector to an iterable
        std::vector<Link*> links = const_cast<Activation*>(this)->getOutputLinks();
        std::vector<Obj*> objs;
        for (Link* link : links) {
            objs.push_back(static_cast<Obj*>(link));
        }
        return new VectorObjectIterable(objs);
    } else {
        throw std::runtime_error("Invalid Relation: " + rel->getRelationName());
    }
}

Obj* Activation::followSingleRelation(const Relation* rel) {
    if (rel->getRelationName() == "SELF") {
        return this;
    } else if (rel->getRelationName() == "NEURON") {
        return neuron;
    } else {
        throw std::runtime_error("Invalid Relation");
    }
}

ActivationKey Activation::getKey() {
    return ActivationKey(neuron->getId(), id);
}

Activation* Activation::getParent() {
    return parent;
}

void Activation::addOutputLink(Link* l) {
    Activation* oAct = l->getOutput();
    assert(outputLinks.find(oAct->getId()) == outputLinks.end());
    outputLinks[oAct->getId()] = l;
}

BindingSignal* Activation::getBindingSignal(BSType s) {
    return bindingSignals[s];
}

std::map<BSType, BindingSignal*> Activation::getBindingSignals() {
    return bindingSignals;
}

bool Activation::hasConflictingBindingSignals(std::map<BSType, BindingSignal*> targetBindingSignals) {
    for (const auto& e : targetBindingSignals) {
        if (isConflictingBindingSignal(e.first, e.second)) {
            return true;
        }
    }
    return false;
}

bool Activation::isConflictingBindingSignal(BSType s, BindingSignal* targetBS) {
    BindingSignal* bs = bindingSignals[s];
    return bs != nullptr && targetBS != bs;
}

bool Activation::hasNewBindingSignals(std::map<BSType, BindingSignal*> targetBindingSignals) {
    for (const auto& e : targetBindingSignals) {
        if (bindingSignals.find(e.first) == bindingSignals.end()) {
            return true;
        }
    }
    return false;
}

Activation* Activation::branch(std::map<BSType, BindingSignal*> bindingSignals) {
    // TODO: Check: Is it necessary to remove the parents binding-signals beforehand?
    std::map<BSType, BindingSignal*> newBindingSignals = bindingSignals;
    for (const auto& bs : getBindingSignals()) {
        newBindingSignals.erase(bs.first);
    }

    return neuron->createActivation(this, getDocument(), newBindingSignals);
}

void Activation::linkOutgoing() {
    neuron->wakeupPropagable();

    for (auto& s : neuron->getOutputSynapses()) {
        if (static_cast<SynapseDefinition*>(s->getType())->isOutgoingLinkingCandidate(getBindingSignals().keySet())) {
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
    std::map<BSType, BindingSignal*> bindingSignals = targetSyn->transitionForward(getBindingSignals());
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

int Activation::getId() {
    return id;
}

Timestamp Activation::getCreated() {
    return created;
}

void Activation::setCreated(Timestamp ts) {
    created = ts;
}

Timestamp Activation::getFired() {
    return fired;
}

void Activation::setFired() {
    fired = doc->getCurrentTimestamp();
}

void Activation::setFired(Timestamp f) {
    fired = f;
}

void Activation::updateFiredStep(Field* net) {
    if (!net->exceedsThreshold() || fired != Timestamp::NOT_SET) {
        return;
    }

    if (firedStep->isQueued()) {
        doc->removeStep(firedStep);
    }

    firedStep->updateNet(net->getUpdatedValue());
    doc->addStep(firedStep);
}

Queue* Activation::getQueue() {
    return doc;
}

Neuron* Activation::getNeuron() {
    return neuron;
}

Document* Activation::getDocument() {
    return doc;
}

Model* Activation::getModel() {
    return neuron->getModel();
}

Link* Activation::getCorrespondingInputLink(Link* l) {
    return nullptr;
}

Link* Activation::getCorrespondingOutputLink(Link* l) {
    return nullptr;
}

std::vector<Link*> Activation::getInputLinks(LinkDefinition* linkDefinition) {
    return getInputLinks();
}

std::vector<Link*> Activation::getOutputLinks(LinkDefinition* linkDefinition) {
    return getOutputLinks();
}

std::vector<Link*> Activation::getOutputLinks() {
    std::vector<Link*> result;
    for (const auto& pair : outputLinks) {
        result.push_back(pair.second);
    }
    return result;
}

Link* Activation::getOutputLink(Neuron* n) {
    return outputLinks[n->getId()];
}

std::vector<Link*> Activation::getOutputLinks(Synapse* s) {
    std::vector<Link*> result;
    for (auto& l : getOutputLinks()) {
        if (l->getSynapse() == s) {
            result.push_back(l);
        }
    }
    return result;
}

int Activation::compareTo(Activation* act) {
    return ID_COMPARATOR(this, act) ? -1 : (ID_COMPARATOR(act, this) ? 1 : 0);
}

bool Activation::equals(Activation* o) {
    return this == o || (o != nullptr && id == o->id);
}

int Activation::hashCode() {
    return std::hash<int>()(id);
}

std::string Activation::toString() {
    return type->getName() + " " + toKeyString();
}

std::string Activation::toKeyString() {
    return "id:" + std::to_string(getId()) + " n:[" + neuron->toKeyString() + "]";
} 