#ifndef NETWORK_ACTIVATION_H
#define NETWORK_ACTIVATION_H


#include "fields/object.h"

#include "network/types/activation_type.h"
#include "network/element.h"
#include "network/model_provider.h"
#include "network/neuron.h"

// Forward declarations to avoid circular includes
class BindingSignal;
class Link;
class Fired;
class Synapse;
class LinkType;
class ActivationKey;

#include <map>
#include <set>
#include <vector>

class Context;

class Activation : public Object, public Element, public ModelProvider {
public:
    static const std::function<bool(Activation*, Activation*)> ID_COMPARATOR;

    Activation(ActivationType* t, Activation* parent, int id, Neuron* n, Context* ctx, BindingSignal** bindingSignals);
    ~Activation();

    RelatedObjectIterable* followManyRelation(Relation* rel) const override;
    Object* followSingleRelation(const Relation* rel) const override;
    
    ActivationKey getKey() const;
    Activation* getParent() const;

    BindingSignal* getBindingSignal(int slot) const;
    BindingSignal** getBindingSignalsArray() const;
    std::set<BindingSignal*> getBindingSignals() const;

    int getId() const;
    long getCreated() const override;
    void setCreated(long ts);
    long getFired() const override;
    void setFired();
    void setFired(long f);
    void updateFiredStep(Field* net);
    Queue* getQueue() const override;
    Neuron* getNeuron() const;
    Context* getContext() const;
    Model* getModel() const override;
    Config* getConfig() const override;

    void addInputLink(Link* l);
    std::vector<Link*> getInputLinks(LinkType* linkDefinition) const;
    std::vector<Link*> getInputLinks() const;
    
    // Method to create InputKey for softmax lookup using output synapse and output activation
    std::vector<int> createInputKeyFromOutputCandidate(Synapse* outputSynapse, Activation* outputActivation) const;

    void addOutputLink(Link* l);
    std::vector<Link*> getOutputLinks(LinkType* linkDefinition) const;
    std::vector<Link*> getOutputLinks() const;
    Link* getOutputLink(Neuron* n) const;
    std::vector<Link*> getOutputLinks(Synapse* s) const;

    int compareTo(Activation* act) const;
    bool equals(Activation* o) const;
    int hashCode() const;
    std::string toString() const;
    std::string toKeyString() const;

private:
    std::vector<int> getInputKey(Link* l) const;
    std::vector<int> getOutputKey(Link* l) const;

    int id;
    Neuron* neuron;
    Context* ctx;
    BindingSignal** bindingSignals;  // Fixed-size array
    Activation* parent;
    long created;
    long fired;
    Fired* firedStep;
    std::map<std::vector<int>, Link*> inputLinks;
    std::map<std::vector<int>, Link*> outputLinks;
};

#endif // NETWORK_ACTIVATION_H 