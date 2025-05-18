#ifndef NETWORK_SYNAPSE_H
#define NETWORK_SYNAPSE_H

#include "fields/type_registry.h"
#include "fields/obj.h"

#include "network/synapse_definition.h"
#include "network/element.h"
#include "network/neuron_reference.h"
#include "network/binding_signal.h"
#include "network/link.h"
#include "network/timestamp.h"
// Forward declarations
class NetworkDirection;
class Model;

#include <map>
#include <vector>

class Synapse : public Obj, public Element {
public:
    Synapse(SynapseDefinition* type);
    Synapse(SynapseDefinition* type, Neuron* input, Neuron* output);

    virtual ~Synapse() = default;

    virtual RelatedObjectIterable* followManyRelation(Relation* rel) const override = 0;
    virtual Obj* followSingleRelation(const Relation* rel) const override = 0;

    int getSynapseId() const;
    void setSynapseId(int synapseId);

    std::map<BSType*, BindingSignal*> transitionForward(const std::map<BSType*, BindingSignal*>& inputBindingSignals);

    Synapse* setPropagable(Model* m, bool propagable);
    bool isPropagable() const;

    void setModified(Model* m);

    void setInput(Neuron* n);
    void setOutput(Neuron* n);

    Synapse* link(Model* m, Neuron* input, Neuron* output);
    void link(Model* m);
    void unlinkInput(Model* m);
    void unlinkOutput(Model* m);

    Link* createLink(Activation* input, Activation* output);
    Link* createLink(Activation* input, const std::map<BSType*, BindingSignal*>& bindingSignals, Activation* output);

    NetworkDirection* getStoredAt() const;

    NeuronReference* getInputRef() const;
    NeuronReference* getOutputRef() const;

    Neuron* getInput() const;
    Neuron* getInput(Model* m) const;
    Neuron* getOutput() const;
    Neuron* getOutput(Model* m) const;

/*
    virtual void write(DataOutput* out) const = 0;
    static Synapse* read(DataInput* in, TypeRegistry* tr);
    virtual void readFields(DataInput* in, TypeRegistry* tr) = 0;
*/

    long getCreated() const override;
    long getFired() const override;

    void deleteSynapse(Model* m);

    Queue* getQueue() const override;

    std::string toString() const;
    std::string toKeyString() const;

protected:
    int synapseId;
    NeuronReference* input;
    NeuronReference* output;

    bool propagable;
};

#endif // NETWORK_SYNAPSE_H 