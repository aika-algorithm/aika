#ifndef NETWORK_CONJUNCTIVE_ACTIVATION_H
#define NETWORK_CONJUNCTIVE_ACTIVATION_H

#include "network/activation.h"
#include <map>

class ConjunctiveActivation : public Activation {
public:
    ConjunctiveActivation(ActivationType* t, Activation* parent, int id, Neuron* n, Document* doc, std::map<int, BindingSignal*> bindingSignals);
    virtual ~ConjunctiveActivation();

    RelatedObjectIterable* followManyRelation(Relation* rel) const override;
    
    void linkIncoming(Activation* excludedInputAct) override;
    void linkIncoming(Synapse* targetSyn, Activation* excludedInputAct);
    void addInputLink(Link* l) override;
    std::vector<Link*> getInputLinks() const override;

private:
    std::map<int, Link*> inputLinks;
};

#endif // NETWORK_CONJUNCTIVE_ACTIVATION_H 