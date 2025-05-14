#include "network/save.h"
#include "fields/step.h"

void Save::add(Neuron* n) {
    Step::add(new Save(n));
}

Save::Save(Neuron* n) : ElementStep(n) {}

const Phase& Save::getPhase() const {
    return Phase::SAVE;
}

void Save::process() {
    // Cast the Element* to Neuron* before calling save()
    Neuron* neuron = static_cast<Neuron*>(getElement());
    neuron->save();
} 