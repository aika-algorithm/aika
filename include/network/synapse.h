#ifndef NETWORK_SYNAPSE_H
#define NETWORK_SYNAPSE_H

#include "fields/type_registry.h"
#include "fields/object.h"

#include "network/types/synapse_type.h"
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

class Synapse : public Object, public Element {
public:
    Synapse(SynapseType* type);
    Synapse(SynapseType* type, Neuron* input, Neuron* output);

    ~Synapse() = default;

    RelatedObjectIterable* followManyRelation(Relation* rel) const override;
    Object* followSingleRelation(const Relation* rel) const override;

    int getSynapseId() const;
    void setSynapseId(int synapseId);

    BindingSignal** transitionForward(const BindingSignal** inputBindingSignals);
    BindingSignal** transitionBackward(const BindingSignal** outputBindingSignals);

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
    bool hasLink(Activation* input, Activation* output) const;

    NetworkDirection* getStoredAt() const;

    NeuronReference* getInputRef() const;
    NeuronReference* getOutputRef() const;

    Neuron* getInput() const;
    Neuron* getInput(Model* m) const;
    Neuron* getOutput() const;
    Neuron* getOutput(Model* m) const;

    long getCreated() const override;
    long getFired() const override;

    void deleteSynapse(Model* m);

    Queue* getQueue() const override;

    std::string toString() const;
    std::string toKeyString() const;
    
    // Methods for paired synapses
    Synapse* getPairedSynapseInputSide() const;
    void setPairedSynapseInputSide(Synapse* pairedSynapseInputSide);
    Synapse* getPairedSynapseOutputSide() const;
    void setPairedSynapseOutputSide(Synapse* pairedSynapseOutputSide);

protected:
    int synapseId;
    NeuronReference* input;
    NeuronReference* output;

    bool propagable;
    
    // Paired synapses
    // Caution: paired Synapses need to be stored and loaded at the same time.
    Synapse* pairedSynapseInputSide;   // Synapse of the paired input-side synapse
    Synapse* pairedSynapseOutputSide;  // Synapse of the paired output-side synapse
};

#endif // NETWORK_SYNAPSE_H 