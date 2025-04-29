#ifndef NETWORK_SAVE_H
#define NETWORK_SAVE_H

#include "network/element_step.h"
#include "network/neuron.h"
#include "network/phase.h"

class Save : public ElementStep<Neuron> {
public:
    static void add(Neuron* n);

    Save(Neuron* n);

    Phase getPhase() override;
    void process() override;
};

#endif // NETWORK_SAVE_H 