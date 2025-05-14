#include "network/neuron.h"
#include "network/binding_signal.h"
#include "network/read_write_lock.h"
#include "network/type.h"
#include "network/model.h"
#include "network/document.h"
#include "network/model_provider.h"
#include "network/type_registry.h"
#include "network/relations.h"
#include "network/neuron_definition.h"
#include "network/synapse_definition.h"
#include "network/activation.h"
#include "network/element.h"
#include "network/obj_impl.h"
#include "network/queue.h"
#include "network/queue_provider.h"
#include "network/timestamp.h"
#include "network/save.h"
#include <iostream>

Neuron::Neuron(NeuronDefinition* type, Model* model, long id)
    : ObjImpl(type), model(model), id(id), synapseIdCounter(0), lastUsed(0), modified(false) {}

Neuron::Neuron(NeuronDefinition* type, Model* model)
    : Neuron(type, model, model->createNeuronId()) {}
    
RelatedObjectIterable* Neuron::followManyRelation(Relation* rel) const {
    if (rel->getRelationName() == "INPUT_SYNAPSES") {
        // Convert input synapses to a vector of Obj*
        std::vector<Obj*> objs;
        for (const auto& pair : inputSynapses) {
            objs.push_back(static_cast<Obj*>(pair.second));
        }
        return new VectorObjectIterable(objs);
    } else if (rel->getRelationName() == "OUTPUT_SYNAPSES") {
        // Convert output synapses to a vector of Obj*
        std::vector<Obj*> objs;
        for (const auto& pair : outputSynapses) {
            objs.push_back(static_cast<Obj*>(pair.second));
        }
        return new VectorObjectIterable(objs);
    } else {
        throw std::runtime_error("Invalid Relation for Neuron: " + rel->getRelationName());
    }
}

Obj* Neuron::followSingleRelation(const Relation* rel) {
    if (rel->getRelationName() == "SELF") {
        return this;
    } else if (rel->getRelationName() == "MODEL") {
        return model;
    } else {
        throw std::runtime_error("Invalid Relation for Neuron: " + rel->getRelationName());
    }
}

long Neuron::getId() const {
    return id;
}

void Neuron::updatePropagable(Neuron* n, bool isPropagable) {
    if (isPropagable) {
        addPropagable(n);
    } else {
        removePropagable(n);
    }
}

void Neuron::addPropagable(Neuron* n) {
    outputLock.acquireWriteLock();
    propagable[n->getId()] = new NeuronReference(n, PROPAGABLE);
    outputLock.releaseWriteLock();
}

void Neuron::removePropagable(Neuron* n) {
    outputLock.acquireWriteLock();
    NeuronReference* nRef = propagable[n->getId()];
    nRef->suspendNeuron();
    propagable.erase(n->getId());
    outputLock.releaseWriteLock();
}

void Neuron::wakeupPropagable() {
    outputLock.acquireReadLock();
    for (auto& nr : propagable) {
        nr.second->getNeuron(model);
    }
    outputLock.releaseReadLock();
}

std::vector<NeuronReference*> Neuron::getPropagable() const {
    std::vector<NeuronReference*> result;
    for (const auto& p : propagable) {
        result.push_back(p.second);
    }
    return result;
}

int Neuron::getNewSynapseId() {
    return synapseIdCounter++;
}

Activation* Neuron::createActivation(Activation* parent, Document* doc, std::map<BSType*, BindingSignal*> bindingSignals) {
    return static_cast<NeuronDefinition*>(getType())->getActivation()->instantiate(doc->createActivationId(), parent, this, doc, bindingSignals);
}

void Neuron::deleteNeuron() {
    std::cout << "Delete Neuron: " << toString() << std::endl;
    inputLock.acquireReadLock();
    for (auto& s : getInputSynapses()) {
        s->unlinkInput(model);
    }
    inputLock.releaseReadLock();

    outputLock.acquireReadLock();
    for (auto& s : getOutputSynapses()) {
        s->unlinkOutput(model);
    }
    outputLock.releaseReadLock();
}

Model* Neuron::getModel() const {
    return model;
}

Config* Neuron::getConfig() const {
    return model->getConfig();
}

void Neuron::setModified() {
    if (!modified) {
        Save::add(this);
    }
    modified = true;
}

void Neuron::resetModified() {
    modified = false;
}

bool Neuron::isModified() const {
    return modified;
}

Synapse* Neuron::getSynapseBySynId(int synId) const {
    return inputSynapses.at(synId);
}

void Neuron::addInputSynapse(Synapse* s) {
    inputLock.acquireWriteLock();
    inputSynapses[s->getSynapseId()] = s;
    inputLock.releaseWriteLock();
}

void Neuron::removeInputSynapse(Synapse* s) {
    inputLock.acquireWriteLock();
    inputSynapses.erase(s->getSynapseId());
    inputLock.releaseWriteLock();
}

void Neuron::addOutputSynapse(Synapse* s) {
    outputLock.acquireWriteLock();
    outputSynapses[s->getOutputRef()->getId()] = s;
    outputLock.releaseWriteLock();
}

void Neuron::removeOutputSynapse(Synapse* s) {
    outputLock.acquireWriteLock();
    outputSynapses.erase(s->getOutputRef()->getId());
    outputLock.releaseWriteLock();
}

std::vector<Synapse*> Neuron::getInputSynapses() const {
    std::vector<Synapse*> result;
    for (const auto& s : inputSynapses) {
        result.push_back(s.second);
    }
    return result;
}

std::vector<Synapse*> Neuron::getOutputSynapses() const {
    std::vector<Synapse*> result;
    for (const auto& s : outputSynapses) {
        result.push_back(s.second);
    }
    return result;
}

Synapse* Neuron::getOutputSynapse(Neuron* n) const {
    outputLock.acquireReadLock();
    Synapse* syn = nullptr;
    for (const auto& s : getOutputSynapses()) {
        if (s->getOutputRef()->getId() == n->getId()) {
            syn = s;
            break;
        }
    }
    outputLock.releaseReadLock();
    return syn;
}

std::vector<Synapse*> Neuron::getInputSynapsesStoredAtOutputSide() const {
    inputLock.acquireReadLock();
    std::vector<Synapse*> result;
    for (const auto& s : getInputSynapses()) {
        if (s->getStoredAt() == OUTPUT) {
            result.push_back(s);
        }
    }
    inputLock.releaseReadLock();
    return result;
}

std::vector<Synapse*> Neuron::getOutputSynapsesStoredAtInputSide() const {
    outputLock.acquireReadLock();
    std::vector<Synapse*> result;
    for (const auto& s : getOutputSynapses()) {
        if (s->getStoredAt() == INPUT) {
            result.push_back(s);
        }
    }
    outputLock.releaseReadLock();
    return result;
}

Synapse* Neuron::getInputSynapse(Neuron* n) const {
    inputLock.acquireReadLock();
    Synapse* syn = nullptr;
    for (const auto& s : getInputSynapses()) {
        if (s->getInputRef()->getId() == n->getId()) {
            syn = s;
            break;
        }
    }
    inputLock.releaseReadLock();
    return syn;
}

Synapse* Neuron::getInputSynapseByType(Type* synapseType) const {
    inputLock.acquireReadLock();
    Synapse* syn = nullptr;
    for (const auto& s : getInputSynapses()) {
        if (synapseType->isInstanceOf(s)) {
            syn = s;
            break;
        }
    }
    inputLock.releaseReadLock();
    return syn;
}

Synapse* Neuron::getOutputSynapseByType(Type* synapseType) const {
    outputLock.acquireReadLock();
    Synapse* syn = nullptr;
    for (const auto& s : getOutputSynapses()) {
        if (synapseType->isInstanceOf(s)) {
            syn = s;
            break;
        }
    }
    outputLock.releaseReadLock();
    return syn;
}

Synapse* Neuron::selectInputSynapse(std::function<bool(Synapse*)> predicate) const {
    inputLock.acquireReadLock();
    Synapse* syn = nullptr;
    for (const auto& s : getInputSynapses()) {
        if (predicate(s)) {
            syn = s;
            break;
        }
    }
    inputLock.releaseReadLock();
    return syn;
}

long Neuron::getCreated() const {
    return -1; // Minimum timestamp value
}

long Neuron::getFired() const {
    return 9223372036854775807L; // MAX_LONG value as maximum timestamp
}

Queue* Neuron::getQueue() const {
    return model;
}

void Neuron::increaseRefCount(RefType rt) {
    std::lock_guard<std::mutex> lock(refCountMutex);
    refCount++;
    refCountByType[rt]++;
}

void Neuron::decreaseRefCount(RefType rt) {
    std::lock_guard<std::mutex> lock(refCountMutex);
    refCount--;
    refCountByType[rt]--;
}

int Neuron::getRefCount() const {
    std::lock_guard<std::mutex> lock(refCountMutex);
    return refCount;
}

bool Neuron::isReferenced() const {
    std::lock_guard<std::mutex> lock(refCountMutex);
    return refCount > 0;
}

long Neuron::getLastUsed() const {
    return lastUsed;
}

void Neuron::updateLastUsed(long docId) {
    lastUsed = std::max(lastUsed, docId);
}

void Neuron::save() {
    // Implement save logic
}

void Neuron::write(std::ostream& out) const {
    out << getClass()->getCanonicalName() << std::endl;
    ObjImpl::write(out);
    for (const auto& s : getInputSynapsesStoredAtOutputSide()) {
        out << true << std::endl;
        s->write(out);
    }
    out << false << std::endl;
    for (const auto& s : getOutputSynapsesStoredAtInputSide()) {
        out << true << std::endl;
        s->write(out);
    }
    out << false << std::endl;
    for (const auto& np : propagable) {
        out << true << std::endl;
        out << np.second->getId() << std::endl;
    }
    out << false << std::endl;
    out << synapseIdCounter << std::endl;
}

Neuron* Neuron::read(std::istream& in, TypeRegistry* tr) {
    short neuronTypeId;
    in >> neuronTypeId;
    NeuronDefinition* neuronDefinition = static_cast<NeuronDefinition*>(tr->getType(neuronTypeId));
    Neuron* n = neuronDefinition->instantiate(static_cast<Model*>(tr));
    n->readFields(in, tr);
    return n;
}

void Neuron::readFields(std::istream& in, TypeRegistry* tr) {
    ObjImpl::readFields(in, tr);
    while (in.get()) {
        Synapse* syn = Synapse::read(in, tr);
        syn->link(static_cast<Model*>(tr));
    }
    while (in.get()) {
        Synapse* syn = Synapse::read(in, tr);
        syn->link(static_cast<Model*>(tr));
    }
    while (in.get()) {
        NeuronReference* nRef = new NeuronReference(in.get(), PROPAGABLE);
        propagable[nRef->getId()] = nRef;
    }
    in >> synapseIdCounter;
}

bool Neuron::operator==(const Neuron& other) const {
    return id == other.id;
}

int Neuron::hashCode() const {
    return std::hash<long>()(id);
}

int Neuron::compareTo(const Neuron& n) const {
    return id < n.id ? -1 : (id > n.id ? 1 : 0);
}

std::string Neuron::toString() const {
    return getType()->getName() + " " + toKeyString();
}

std::string Neuron::toKeyString() const {
    if (this == MIN_NEURON) return "MIN_NEURON";
    if (this == MAX_NEURON) return "MAX_NEURON";
    return std::to_string(getId());
}