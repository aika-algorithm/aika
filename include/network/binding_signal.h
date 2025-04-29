#ifndef NETWORK_BINDING_SIGNAL_H
#define NETWORK_BINDING_SIGNAL_H

#include <map>
#include <set>
#include "network/activation.h"
#include "network/activation_key.h"
#include "document.h"
#include "neuron.h"

class BindingSignal {
public:
    BindingSignal(int tokenId, Document* doc);
    int getTokenId() const;
    Document* getDocument() const;
    void addActivation(Activation* act);
    std::set<Activation*> getActivations(Neuron* n);
    std::set<Activation*> getActivations();
    std::string toString() const;

private:
    int tokenId;
    Document* doc;
    std::map<ActivationKey, Activation*, ActivationKeyComparator> activations;
};

#endif // NETWORK_BINDING_SIGNAL_H 