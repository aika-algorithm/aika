#ifndef NETWORK_NEURON_H
#define NETWORK_NEURON_H

#include "fields/object.h"

#include "network/neuron_type.h"
#include "network/element.h"
#include "network/model_provider.h"
#include "network/read_write_lock.h"
#include "network/ref_type.h" // Include RefType enum

// Forward declarations to break circular dependencies
class Activation;
class Synapse;
class BSType;
class BindingSignal;
class Document;
class NeuronReference; // Forward declare NeuronReference
#include <map>
#include <set>
#include <vector>
#include <string>

class Neuron : public Object, public Element, public ModelProvider {
public:
    Neuron(NeuronType* type, Model* model, long id);
    Neuron(NeuronType* type, Model* model);

    RelatedObjectIterable* followManyRelation(Relation* rel) const override;
    Object* followSingleRelation(const Relation* rel) const override;
    long getId() const;
    void updatePropagable(Neuron* n, bool isPropagable);
    void addPropagable(Neuron* n);
    void removePropagable(Neuron* n);
    void wakeupPropagable();
    std::set<NeuronReference*> getPropagable() const;
    int getNewSynapseId();
    Activation* createActivation(Activation* parent, Document* doc, std::map<BSType*, BindingSignal*> bindingSignals);
    void deleteNeuron();
    Model* getModel() const override;
    Config* getConfig() const override;
    void setModified();
    void resetModified();
    bool isModified() const;
    Synapse* getSynapseBySynId(int synId) const;
    void addInputSynapse(Synapse* s);
    void removeInputSynapse(Synapse* s);
    void addOutputSynapse(Synapse* s);
    void removeOutputSynapse(Synapse* s);
    std::vector<Synapse*> getInputSynapses() const;
    std::vector<Synapse*> getOutputSynapses() const;
    std::vector<Synapse*> getInputSynapsesAsStream() const;
    std::vector<Synapse*> getOutputSynapsesAsStream() const;
    Synapse* getOutputSynapse(Neuron* n) const;
    std::vector<Synapse*> getInputSynapsesStoredAtOutputSide() const;
    std::vector<Synapse*> getOutputSynapsesStoredAtInputSide() const;
    Synapse* getInputSynapse(Neuron* n) const;
    Synapse* getInputSynapseByType(Type* synapseType) const;
    std::vector<Synapse*> getInputSynapsesByType(Type* synapseType) const;
    Synapse* getOutputSynapseByType(Type* synapseType) const;
    std::vector<Synapse*> getOutputSynapsesByType(Type* synapseType) const;
    Synapse* selectInputSynapse(std::function<bool(Synapse*)> predicate) const;
    long getCreated() const override;
    long getFired() const override;
    Queue* getQueue() const override;
    void increaseRefCount(RefType rt);
    void decreaseRefCount(RefType rt);
    int getRefCount() const;
    bool isReferenced() const;
    long getLastUsed() const;
    void updateLastUsed(long docId);
    void save();
    void write(std::ostream& out) const;
    static Neuron* read(std::istream& in, TypeRegistry* tr);
    void readFields(std::istream& in, TypeRegistry* tr);
    bool operator==(const Neuron& other) const;
    bool operator!=(const Neuron& other) const;
    int compareTo(const Neuron& n) const;
    std::string toString() const;
    std::string toKeyString() const;

private:
    Model* model;
    long id;
    int synapseIdCounter;
    ReadWriteLock inputLock;
    std::map<int, Synapse*> inputSynapses;
    ReadWriteLock outputLock;
    std::map<long, Synapse*> outputSynapses;
    std::map<long, NeuronReference*> propagable;
    int refCount;
    int refCountByType[static_cast<int>(RefType::OTHER) + 1];
    long lastUsed;
    bool modified;
};

#endif // NETWORK_NEURON_H 