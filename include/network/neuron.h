#ifndef NETWORK_NEURON_H
#define NETWORK_NEURON_H

#include "fields/obj.h"

#include "network/neuron_definition.h"
#include "network/element.h"
#include "network/model_provider.h"
#include "network/read_write_lock.h"
#include "network/neuron_reference.h"
#include "network/synapse.h"
#include <map>
#include <set>
#include <vector>
#include <string>

class Neuron : public Obj, public Element, public ModelProvider {
public:
    Neuron(NeuronDefinition* type, Model* model, long id);
    Neuron(NeuronDefinition* type, Model* model);

    Stream<Obj*> followManyRelation(Relation* rel) override;
    Obj* followSingleRelation(Relation* rel) override;
    long getId() const;
    void updatePropagable(Neuron* n, bool isPropagable);
    void wakeupPropagable();
    std::set<NeuronReference*> getPropagable() const;
    int getNewSynapseId();
    Activation* createActivation(Activation* parent, Document* doc, std::map<BSType*, BindingSignal*> bindingSignals);
    void deleteNeuron();
    Model* getModel() override;
    void setModified();
    void resetModified();
    bool isModified() const;
    Synapse* getSynapseBySynId(int synId) const;
    void addInputSynapse(Synapse* s);
    void removeInputSynapse(Synapse* s);
    void addOutputSynapse(Synapse* s);
    void removeOutputSynapse(Synapse* s);
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
    Timestamp getCreated() override;
    Timestamp getFired() override;
    Queue* getQueue() override;
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
    int refCountByType[RefType::OTHER + 1];
    long lastUsed;
    bool modified;
};

#endif // NETWORK_NEURON_H 