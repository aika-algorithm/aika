#ifndef NETWORK_INHIBITORY_ACTIVATION_H
#define NETWORK_INHIBITORY_ACTIVATION_H

#include "network/activation.h"
#include "network/link.h"
#include <map>
#include <vector>

class InhibitoryActivation : public Activation {
public:
    InhibitoryActivation(ActivationDefinition* t, Activation* parent, int id, Neuron* n, Document* doc, std::map<BSType*, BindingSignal*> bindingSignals);

    void addInputLink(Link* l) override;
    Link* getInputLink(int bsId);
    int getInputKey(Link* l);
    void addOutputLink(Link* l) override;
    Link* getOutputLink(int bsId);
    int getOutputKey(Link* l);
    void linkIncoming(Activation* excludedInputAct) override;
    std::vector<Link*> getInputLinks() override;
    std::vector<Link*> getOutputLinks() override;
    Link* getCorrespondingInputLink(Link* l) override;
    Link* getCorrespondingOutputLink(Link* l) override;

private:
    std::map<int, Link*> inputLinks;
    std::map<int, Link*> outputLinks;
};

#endif // NETWORK_INHIBITORY_ACTIVATION_H 