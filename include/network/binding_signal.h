#ifndef NETWORK_BINDING_SIGNAL_H
#define NETWORK_BINDING_SIGNAL_H

#include <map>
#include <set>
#include "network/activation_key.h"
#include "network/neuron.h"

class Activation;
class Context;

class BindingSignal {
public:
    BindingSignal(int tokenId, Context* ctx);
    int getTokenId() const;
    Context* getContext() const;
    void addActivation(Activation* act);
    std::set<Activation*> getActivations(Neuron* n);
    std::set<Activation*> getActivations();
    std::string toString() const;

private:
    int tokenId;
    Context* ctx;
    std::map<ActivationKey, Activation*, ActivationKeyComparator> activations;
};

#endif // NETWORK_BINDING_SIGNAL_H 