#ifndef NETWORK_DISJUNCTIVE_ACTIVATION_H
#define NETWORK_DISJUNCTIVE_ACTIVATION_H

#include "network/activation.h"
#include <map>

class DisjunctiveActivation : public Activation {
public:
    DisjunctiveActivation(ActivationDefinition* t, Activation* parent, int id, Neuron* n, Document* doc, std::map<BSType*, BindingSignal*> bindingSignals);
    virtual ~DisjunctiveActivation();

    RelatedObjectIterable* followManyRelation(Relation* rel) const override;
    
    void linkIncoming(Activation* excludedInputAct) override;
    void addInputLink(Link* l) override;
    std::vector<Link*> getInputLinks() const override;

private:
    std::map<int, Link*> inputLinks;
};

#endif // NETWORK_DISJUNCTIVE_ACTIVATION_H 