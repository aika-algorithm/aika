#ifndef NETWORK_ACTIVATION_H
#define NETWORK_ACTIVATION_H


#include "fields/obj.h"

#include "network/activation_definition.h"
#include "network/element.h"
#include "network/model_provider.h"
#include "network/neuron.h"
#include "network/bs_type.h"
#include "network/binding_signal.h"
#include "network/link.h"
#include "network/fired.h"

#include <map>
#include <set>
#include <vector>

class Document;

class Activation : public Obj, public Element, public ModelProvider {
public:
    static const std::function<bool(Activation*, Activation*)> ID_COMPARATOR;

    Activation(ActivationDefinition* t, Activation* parent, int id, Neuron* n, Document* doc, std::map<BSType, BindingSignal*> bindingSignals);
    virtual ~Activation();

    // Implementation of Obj virtual methods
    RelatedObjectIterable* followManyRelation(Relation* rel) const override;
    Obj* followSingleRelation(const Relation* rel) override;
    
    ActivationKey getKey();
    Activation* getParent();
    void addOutputLink(Link* l);
    virtual void addInputLink(Link* l) = 0;
    BindingSignal* getBindingSignal(BSType s);
    std::map<BSType, BindingSignal*> getBindingSignals();
    bool hasConflictingBindingSignals(std::map<BSType, BindingSignal*> targetBindingSignals);
    bool isConflictingBindingSignal(BSType s, BindingSignal* targetBS);
    bool hasNewBindingSignals(std::map<BSType, BindingSignal*> targetBindingSignals);
    Activation* branch(std::map<BSType, BindingSignal*> bindingSignals);
    void linkOutgoing();
    void linkOutgoing(Synapse* targetSyn);
    void propagate(Synapse* targetSyn);
    virtual void linkIncoming(Activation* excludedInputAct) = 0;
    std::set<Activation*> collectLinkingTargets(Neuron* n);
    int getId();
    long getCreated() override;
    void setCreated(long ts);
    long getFired() override;
    void setFired();
    void setFired(long f);
    void updateFiredStep(Field* net);
    Queue* getQueue();
    Neuron* getNeuron();
    Document* getDocument();
    Model* getModel();
    Link* getCorrespondingInputLink(Link* l);
    Link* getCorrespondingOutputLink(Link* l);
    std::vector<Link*> getInputLinks(LinkDefinition* linkDefinition);
    virtual std::vector<Link*> getInputLinks() = 0;
    std::vector<Link*> getOutputLinks(LinkDefinition* linkDefinition);
    std::vector<Link*> getOutputLinks();
    Link* getOutputLink(Neuron* n);
    std::vector<Link*> getOutputLinks(Synapse* s);
    int compareTo(Activation* act);
    bool equals(Activation* o);
    int hashCode();
    std::string toString();
    std::string toKeyString();

protected:
    int id;
    Neuron* neuron;
    Document* doc;
    std::map<BSType, BindingSignal*> bindingSignals;
    Activation* parent;
    Timestamp created;
    Timestamp fired;
    Fired* firedStep;
    std::map<int, Link*> outputLinks;
};

#endif // NETWORK_ACTIVATION_H 