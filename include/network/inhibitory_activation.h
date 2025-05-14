#ifndef NETWORK_INHIBITORY_ACTIVATION_H
#define NETWORK_INHIBITORY_ACTIVATION_H

#include "network/activation.h"
#include "network/link.h"
#include <map>
#include <vector>

class InhibitoryActivation : public Activation {
public:
    InhibitoryActivation(ActivationDefinition* t, Activation* parent, int id, Neuron* n, Document* doc, std::map<BSType*, BindingSignal*> bindingSignals);

    RelatedObjectIterable* followManyRelation(Relation* rel) const override;
    
    void addInputLink(Link* l) override;
    Link* getInputLink(int bsId) const;
    int getInputKey(Link* l) const;
    void addOutputLink(Link* l) override;
    Link* getOutputLink(int bsId) const;
    int getOutputKey(Link* l) const;
    void linkIncoming(Activation* excludedInputAct) override;
    std::vector<Link*> getInputLinks() const override;
    std::vector<Link*> getOutputLinks() const override;
    Link* getCorrespondingInputLink(const Link* l) const override;
    Link* getCorrespondingOutputLink(const Link* l) const override;

private:
    std::map<int, Link*> inputLinks;
    std::map<int, Link*> outputLinks;
};

#endif // NETWORK_INHIBITORY_ACTIVATION_H 