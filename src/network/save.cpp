#include "network/save.h"

void Save::add(Neuron* n) {
    Step::add(new Save(n));
}

Save::Save(Neuron* n) : ElementStep(n) {}

Phase Save::getPhase() {
    return Phase::SAVE;
}

void Save::process() {
    getElement()->save();
} 