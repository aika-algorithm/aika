#ifndef NETWORK_SAVE_H
#define NETWORK_SAVE_H

#include "fields/step.h"
#include "network/element_step.h"
#include "network/neuron.h"
#include "network/phase.h"

class Save : public ElementStep {
public:
    static void add(Neuron* n);

    Save(Neuron* n);
    virtual ~Save() = default;

    const Phase& getPhase() const override;
    void process() override;
};

#endif // NETWORK_SAVE_H 