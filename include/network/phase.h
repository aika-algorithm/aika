#ifndef NETWORK_PHASE_H
#define NETWORK_PHASE_H

#include "fields/queue_key.h"
#include <string>

// First define the enum for Phase types
enum class PhaseType {
    INFERENCE,
    FIRED,
    INSTANTIATION_TRIGGER,
    TRAINING,
    INACTIVE_LINKS,
    SAVE
};

// Phase class that implements ProcessingPhase interface
class Phase final : public ProcessingPhase {
public:
    Phase(PhaseType type) : type(type) {}

    int rank() const override {
        switch (type) {
            case PhaseType::INFERENCE: return 0;
            case PhaseType::FIRED: return 1;
            case PhaseType::INSTANTIATION_TRIGGER: return 2;
            case PhaseType::TRAINING: return 3;
            case PhaseType::INACTIVE_LINKS: return 4;
            case PhaseType::SAVE: return 5;
            default: return 0;
        }
    }

    bool isDelayed() const override {
        return type == PhaseType::SAVE; // Only SAVE phase is delayed
    }

    // For string representation
    std::string toString() const {
        switch (type) {
            case PhaseType::INFERENCE: return "INFERENCE";
            case PhaseType::FIRED: return "FIRED";
            case PhaseType::INSTANTIATION_TRIGGER: return "INSTANTIATION_TRIGGER";
            case PhaseType::TRAINING: return "TRAINING";
            case PhaseType::INACTIVE_LINKS: return "INACTIVE_LINKS";
            case PhaseType::SAVE: return "SAVE";
            default: return "UNKNOWN";
        }
    }

    // Static instances for convenience
    static const Phase INFERENCE;
    static const Phase FIRED;
    static const Phase INSTANTIATION_TRIGGER;
    static const Phase TRAINING;
    static const Phase INACTIVE_LINKS;
    static const Phase SAVE;

private:
    PhaseType type;
};

// Define the static instances
inline const Phase Phase::INFERENCE(PhaseType::INFERENCE);
inline const Phase Phase::FIRED(PhaseType::FIRED);
inline const Phase Phase::INSTANTIATION_TRIGGER(PhaseType::INSTANTIATION_TRIGGER);
inline const Phase Phase::TRAINING(PhaseType::TRAINING);
inline const Phase Phase::INACTIVE_LINKS(PhaseType::INACTIVE_LINKS);
inline const Phase Phase::SAVE(PhaseType::SAVE);

#endif // NETWORK_PHASE_H 