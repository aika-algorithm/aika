#ifndef LINKER_H
#define LINKER_H

#include "network/activation.h"
#include "network/neuron.h"
#include "network/synapse.h"
#include "network/link.h"

class Linker {
public:
    static void linkLatent(Activation* act);

    static bool matchBindingSignals(Activation* act, std::map<int, BindingSignal*> latentBindingSignals);

    static void linkIncoming(Activation* act, Activation* excludedInputAct);
    static void linkIncoming(Activation* act, Synapse* targetSyn, Activation* excludedInputAct);
    static void linkOutgoing(Activation* act);
    static void linkOutgoing(Activation* act, Synapse* targetSyn);
    static void propagate(Activation* act, Synapse* targetSyn);
    static std::set<Activation*> collectLinkingTargets(std::map<int, BindingSignal*> bindingSignals, Neuron* n);
};

#endif //LINKER_H
