#ifndef LINKER_H
#define LINKER_H

#include "network/activation.h"
#include "network/neuron.h"
#include "network/synapse.h"
#include "network/link.h"

class Linker {
public:
    static void linkOutgoing(Activation* act);
    static void linkOutgoing(Activation* act, Synapse* targetSyn);

    static void linkIncoming(Activation* act, Activation* excludedInputAct);
    static void linkIncoming(Activation* act, Synapse* targetSyn, Activation* excludedInputAct);

private:
    static void pairLinking(Activation* act, Synapse* firstSynapse);
    static void propagate(Activation* act, Synapse* targetSyn, BindingSignal** outputBindingSignals);

    static std::set<Activation*> collectLinkingTargets(BindingSignal** bindingSignals, Neuron* n);
};

#endif //LINKER_H
