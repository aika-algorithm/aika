#include "network/neuron.h"
#include "network/binding_signal.h"
#include "network/read_write_lock.h"
#include "fields/type.h"
#include "network/model.h"
#include "network/context.h"
#include "network/model_provider.h"
#include "fields/type_registry.h"
//#include "network/relations.h" // This file doesn't exist, may not be needed
#include "network/types/neuron_type.h"
#include "network/types/synapse_type.h"
#include "network/activation.h"
#include "network/activations_per_context.h"
#include "network/element.h"
#include "fields/object.h"
#include "fields/queue.h"
#include "fields/queue_provider.h"
#include "network/timestamp.h"
#include "network/save.h"
#include "network/synapse.h"
#include "network/input.h"
#include "network/output.h"
#include <iostream>
#include <mutex>

Neuron::Neuron(NeuronType* type, Model* model, long id)
    : Object(type), model(model), id(id), synapseIdCounter(0), lastUsed(0), modified(false) {
    initFields();
}

Neuron::Neuron(NeuronType* type, Model* model)
    : Neuron(type, model, model->createNeuronId()) {
    initFields();
}

Neuron::~Neuron() {
    // Clean up ActivationsPerContext instances
    for (auto& pair : activationsPerContext) {
        delete pair.second;
    }
    activationsPerContext.clear();
}
    
RelatedObjectIterable* Neuron::followManyRelation(Relation* rel) const {
    if (rel->getRelationLabel() == "INPUT_SYNAPSES") {
        // Convert input synapses to a vector of Object*
        std::vector<Object*> objs;
        for (const auto& pair : inputSynapses) {
            // Synapse inherits from Object, but we need to avoid the static_cast to fix the error
            objs.push_back(reinterpret_cast<Object*>(pair.second));
        }
        return new VectorObjectIterable(objs);
    } else if (rel->getRelationLabel() == "OUTPUT_SYNAPSES") {
        // Convert output synapses to a vector of Object*
        std::vector<Object*> objs;
        for (const auto& pair : outputSynapses) {
            // Synapse inherits from Object, but we need to avoid the static_cast to fix the error
            objs.push_back(reinterpret_cast<Object*>(pair.second));
        }
        return new VectorObjectIterable(objs);
    } else {
        throw std::runtime_error("Invalid Relation for Neuron: " + rel->getRelationLabel());
    }
}

Object* Neuron::followSingleRelation(const Relation* rel) const {
    if (rel->getRelationLabel() == "SELF") {
        return const_cast<Neuron*>(this);
    // Model is not an Object subclass, so we can't return it directly
    // For now, return nullptr for "MODEL" relation
    } else if (rel->getRelationLabel() == "MODEL") {
        return nullptr; // Can't return model as it's not an Object
    } else {
        throw std::runtime_error("Invalid Relation for Neuron: " + rel->getRelationLabel());
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
    propagable[n->getId()] = new NeuronReference(n, RefType::PROPAGABLE);
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

std::set<NeuronReference*> Neuron::getPropagable() const {
    std::set<NeuronReference*> result;
    for (const auto& p : propagable) {
        result.insert(p.second);
    }
    return result;
}

int Neuron::getNewSynapseId() {
    return synapseIdCounter++;
}

Activation* Neuron::createActivation(Activation* parent, Context* ctx, std::map<int, BindingSignal*> bindingSignals) {
    // Get the neuron definition and its activation definition
    NeuronType* neuronType = static_cast<NeuronType*>(getType());
    
    ActivationType* activationType = neuronType->getActivationType();
    if (!activationType) {
        return nullptr;
    }
    
    // Create activation ID
    int activationId = ctx->createActivationId();
    
    // For now, create a Activation as the default
    // In a full implementation, this would depend on the activation definition type
    return new Activation(activationType, parent, activationId, this, ctx, bindingSignals);
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
        if (s->getStoredAt() == NetworkDirection::OUTPUT) {
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
        if (s->getStoredAt() == NetworkDirection::INPUT) {
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
        if (synapseType->isInstanceOf(s->getType())) {
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
        if (synapseType->isInstanceOf(s->getType())) {
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

// Adding mutex as a static member since it's not declared in the header
static std::mutex refCountMutex;

void Neuron::increaseRefCount(RefType rt) {
    std::lock_guard<std::mutex> lock(refCountMutex);
    refCount++;
    refCountByType[static_cast<int>(rt)]++;
}

void Neuron::decreaseRefCount(RefType rt) {
    std::lock_guard<std::mutex> lock(refCountMutex);
    refCount--;
    refCountByType[static_cast<int>(rt)]--;
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

void Neuron::updateLastUsed(long ctxId) {
    lastUsed = std::max(lastUsed, ctxId);
}

void Neuron::save() {
    // Implement save logic
}

void Neuron::write(std::ostream& out) const {
    // Write the type name instead of using getClass()->getCanonicalName()
    out << getType()->getName() << std::endl;
    // Object doesn't have a write method, just write our own fields
    for (const auto& s : getInputSynapsesStoredAtOutputSide()) {
        out << true << std::endl;
        // Synapse doesn't have a write method yet, so we'll just write the ID
        out << s->getSynapseId() << std::endl;
    }
    out << false << std::endl;
    for (const auto& s : getOutputSynapsesStoredAtInputSide()) {
        out << true << std::endl;
        // Synapse doesn't have a write method yet, so we'll just write the ID
        out << s->getSynapseId() << std::endl;
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
    NeuronType* neuronType = static_cast<NeuronType*>(tr->getType(neuronTypeId));
    // We can't cast TypeRegistry to Model, so we need to get the model differently
    // For now, we'll use a placeholder or null to indicate this needs to be fixed
    Model* model = nullptr;  // This would need to be fixed with the proper model access
    // Create a simple neuron for now
    Neuron* n = new Neuron(neuronType, model, 0);
    n->readFields(in, tr);
    return n;
}

void Neuron::readFields(std::istream& in, TypeRegistry* tr) {
    // Object doesn't have a readFields method, just read our own fields
    bool hasMore;
    // Read input synapses
    while (in >> hasMore && hasMore) {
        // We need to implement a proper read method in Synapse class
        // For now, we'll just read the synapse ID and skip actual synapse loading
        int synapseId;
        in >> synapseId;
        // In a real implementation, we would create and link the synapse
    }
    // Read output synapses
    while (in >> hasMore && hasMore) {
        // We need to implement a proper read method in Synapse class
        // For now, we'll just read the synapse ID and skip actual synapse loading
        int synapseId;
        in >> synapseId;
        // In a real implementation, we would create and link the synapse
    }
    // Read propagable neurons
    while (in >> hasMore && hasMore) {
        long id;
        in >> id;
        NeuronReference* nRef = new NeuronReference(id, RefType::PROPAGABLE);
        propagable[nRef->getId()] = nRef;
    }
    in >> synapseIdCounter;
}

bool Neuron::operator==(const Neuron& other) const {
    return id == other.id;
}

bool Neuron::operator!=(const Neuron& other) const {
    return !(*this == other);
}

// Removing hashCode method as it's not declared in the header
// int Neuron::hashCode() const {
//     return std::hash<long>()(id);
// }

int Neuron::compareTo(const Neuron& n) const {
    return id < n.id ? -1 : (id > n.id ? 1 : 0);
}

std::string Neuron::toString() const {
    return getType()->getName() + " " + toKeyString();
}

std::string Neuron::toKeyString() const {
    // MIN_NEURON and MAX_NEURON are not defined, replacing with simple ID string
    return std::to_string(getId());
}

// Implement the missing methods from the header
std::vector<Synapse*> Neuron::getInputSynapsesAsStream() const {
    return getInputSynapses();
}

std::vector<Synapse*> Neuron::getOutputSynapsesAsStream() const {
    return getOutputSynapses();
}

std::vector<Synapse*> Neuron::getInputSynapsesByType(Type* synapseType) const {
    inputLock.acquireReadLock();
    std::vector<Synapse*> result;
    for (const auto& s : getInputSynapses()) {
        if (synapseType->isInstanceOf(s->getType())) {
            result.push_back(s);
        }
    }
    inputLock.releaseReadLock();
    return result;
}

std::vector<Synapse*> Neuron::getOutputSynapsesByType(Type* synapseType) const {
    outputLock.acquireReadLock();
    std::vector<Synapse*> result;
    for (const auto& s : getOutputSynapses()) {
        if (synapseType->isInstanceOf(s->getType())) {
            result.push_back(s);
        }
    }
    outputLock.releaseReadLock();
    return result;
}

// ActivationsPerContext management methods
void Neuron::addActivation(Activation* activation) {
    if (!activation) {
        return;
    }
    
    Context* context = activation->getContext();
    if (!context) {
        return;
    }
    
    long contextId = context->getId();
    
    // Get or create ActivationsPerContext for this context
    ActivationsPerContext* apc = getActivationsPerContext(context);
    if (!apc) {
        apc = new ActivationsPerContext(context);
        activationsPerContext[contextId] = apc;
    }
    
    // Add activation to the context
    apc->addActivation(activation);
}

void Neuron::removeActivation(Activation* activation) {
    if (!activation) {
        return;
    }
    
    Context* context = activation->getContext();
    if (!context) {
        return;
    }
    
    long contextId = context->getId();
    auto it = activationsPerContext.find(contextId);
    if (it != activationsPerContext.end()) {
        ActivationsPerContext* apc = it->second;
        apc->removeActivation(activation);
        
        // If this was the last activation, remove and delete ActivationsPerContext
        if (apc->isEmpty()) {
            delete apc;
            activationsPerContext.erase(it);
        }
    }
}

Activation* Neuron::getActivationByTokenIds(Context* context, const std::vector<int>& tokenIds) const {
    if (!context) {
        return nullptr;
    }
    
    ActivationsPerContext* apc = getActivationsPerContext(context);
    return apc ? apc->getActivation(tokenIds) : nullptr;
}

ActivationsPerContext* Neuron::getActivationsPerContext(Context* context) const {
    if (!context) {
        return nullptr;
    }
    
    long contextId = context->getId();
    auto it = activationsPerContext.find(contextId);
    return (it != activationsPerContext.end()) ? it->second : nullptr;
}