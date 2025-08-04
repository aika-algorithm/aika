#ifndef NETWORK_ACTIVATION_H
#define NETWORK_ACTIVATION_H


#include "fields/object.h"

#include "network/activation_type.h"
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

class Document;

class Activation : public Object, public Element, public ModelProvider {
public:
    static const std::function<bool(Activation*, Activation*)> ID_COMPARATOR;

    Activation(ActivationType* t, Activation* parent, int id, Neuron* n, Document* doc, std::map<int, BindingSignal*> bindingSignals);
    virtual ~Activation();

    // Implementation of Object virtual methods
    RelatedObjectIterable* followManyRelation(Relation* rel) const override;
    Object* followSingleRelation(const Relation* rel) const override;
    
    ActivationKey getKey() const;
    Activation* getParent() const;
    void addOutputLink(Link* l);
    virtual void addInputLink(Link* l) = 0;
    BindingSignal* getBindingSignal(int s) const;
    std::map<int, BindingSignal*> getBindingSignals() const;
    bool hasConflictingBindingSignals(std::map<int, BindingSignal*> targetBindingSignals) const;
    bool isConflictingBindingSignal(int s, BindingSignal* targetBS) const;
    bool hasNewBindingSignals(std::map<int, BindingSignal*> targetBindingSignals) const;
    Activation* branch(std::map<int, BindingSignal*> bindingSignals);
    void linkOutgoing();
    void linkOutgoing(Synapse* targetSyn);
    void propagate(Synapse* targetSyn);
    virtual void linkIncoming(Activation* excludedInputAct) = 0;
    std::set<Activation*> collectLinkingTargets(Neuron* n);
    int getId() const;
    long getCreated() const override;
    void setCreated(long ts);
    long getFired() const override;
    void setFired();
    void setFired(long f);
    void updateFiredStep(Field* net);
    Queue* getQueue() const override;
    Neuron* getNeuron() const;
    Document* getDocument() const;
    Model* getModel() const override;
    Config* getConfig() const override;
    Link* getCorrespondingInputLink(const Link* l) const;
    Link* getCorrespondingOutputLink(const Link* l) const;
    std::vector<Link*> getInputLinks(LinkType* linkDefinition) const;
    virtual std::vector<Link*> getInputLinks() const = 0;
    std::vector<Link*> getOutputLinks(LinkType* linkDefinition) const;
    std::vector<Link*> getOutputLinks() const;
    Link* getOutputLink(Neuron* n) const;
    std::vector<Link*> getOutputLinks(Synapse* s) const;
    int compareTo(Activation* act) const;
    bool equals(Activation* o) const;
    int hashCode() const;
    std::string toString() const;
    std::string toKeyString() const;

protected:
    int id;
    Neuron* neuron;
    Document* doc;
    std::map<int, BindingSignal*> bindingSignals;
    Activation* parent;
    long created;
    long fired;
    Fired* firedStep;
    std::map<int, Link*> outputLinks;
};

#endif // NETWORK_ACTIVATION_H 