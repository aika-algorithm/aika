#include "network/fired_queue_key.h"
#include "network/timestamp.h"

FiredQueueKey::FiredQueueKey(int round, ProcessingPhase phase, Element* element, Timestamp currentTimestamp)
    : QueueKey(round, phase, currentTimestamp),
      created(element->getCreated()),
      fired(element->getFired()) {}

Timestamp FiredQueueKey::getFired() const {
    return fired;
}

Timestamp FiredQueueKey::getCreated() const {
    return created;
}

std::string FiredQueueKey::toString() const {
    std::string firedStr = (fired == Timestamp::NOT_SET) ? "NOT_FIRED" : std::to_string(fired);
    return "[r:" + std::to_string(getRound()) + ",p:" + std::to_string(getPhase()) + ",f:" + firedStr + ",c:" + std::to_string(created) + ",ts:" + std::to_string(getCurrentTimestamp()) + "]";
}

int FiredQueueKey::compareTo(QueueKey* qk) const {
    return COMPARATOR(this, static_cast<FiredQueueKey*>(qk));
} 