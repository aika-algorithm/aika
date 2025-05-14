#include "network/fired_queue_key.h"

const std::function<int(const FiredQueueKey*, const FiredQueueKey*)> FiredQueueKey::COMPARATOR = 
    [](const FiredQueueKey* a, const FiredQueueKey* b) -> int {
        if (a->getRound() != b->getRound()) {
            return (a->getRound() < b->getRound()) ? -1 : 1;
        }
        if (a->getPhase().rank() != b->getPhase().rank()) {
            return (a->getPhase().rank() < b->getPhase().rank()) ? -1 : 1;
        }
        if (a->getFired() != b->getFired()) {
            return (a->getFired() < b->getFired()) ? -1 : 1;
        }
        if (a->getCreated() != b->getCreated()) {
            return (a->getCreated() < b->getCreated()) ? -1 : 1; 
        }
        return 0;
    };

FiredQueueKey::FiredQueueKey(int round, const ProcessingPhase& phase, Element* element, long currentTimestamp)
    : QueueKey(round, const_cast<ProcessingPhase&>(phase), currentTimestamp),
      created(element->getCreated()),
      fired(element->getFired()) {}

long FiredQueueKey::getFired() const {
    return fired;
}

long FiredQueueKey::getCreated() const {
    return created;
}

std::string FiredQueueKey::toString() const {
    std::string firedStr = (fired == -1) ? "NOT_FIRED" : std::to_string(fired);
    return "[r:" + std::to_string(getRound()) + ",p:" + std::to_string(getPhase().rank()) + ",f:" + firedStr + ",c:" + std::to_string(created) + ",ts:" + std::to_string(getCurrentTimestamp()) + "]";
}

int FiredQueueKey::compareTo(QueueKey* qk) const {
    return COMPARATOR(this, static_cast<FiredQueueKey*>(qk));
} 