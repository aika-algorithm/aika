#ifndef NETWORK_PHASE_H
#define NETWORK_PHASE_H

#include "network/processing_phase.h"

enum class Phase : public ProcessingPhase {
    INFERENCE,
    FIRED,
    INSTANTIATION_TRIGGER,
    TRAINING,
    INACTIVE_LINKS,
    SAVE
};

#endif // NETWORK_PHASE_H 