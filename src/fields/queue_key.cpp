#include "fields/queue_key.h"
#include <sstream>

const std::function<bool(const std::shared_ptr<QueueKey>, const std::shared_ptr<QueueKey>)> QueueKey::COMPARATOR =
    [](const std::shared_ptr<QueueKey> k1, const std::shared_ptr<QueueKey> k2) -> bool {
        if (k1->getRound() != k2->getRound())
            return k1->getRound() < k2->getRound();
        if (k1->getPhase()->rank() != k2->getPhase()->rank())
            return k1->getPhase()->rank() < k2->getPhase()->rank();
        if (k1->getCurrentTimestamp()->getTimestampValue() != k2->getCurrentTimestamp()->getTimestampValue())
            return k1->getCurrentTimestamp()->getTimestampValue() < k2->getCurrentTimestamp()->getTimestampValue();
        return false;
};

QueueKey::QueueKey(int round, std::shared_ptr<ProcessingPhase> phase, std::shared_ptr<Timestamp> currentTimestamp)
    : round(round), phase(phase), currentTimestamp(currentTimestamp) {}

int QueueKey::getRound() const {
    return round;
}

std::string QueueKey::getRoundStr() const {
    return std::to_string(getRound());
}

std::shared_ptr<ProcessingPhase> QueueKey::getPhase() const {
    return phase;
}

std::string QueueKey::getPhaseStr() const {
    return getPhase()->toString() + "-" + getPhase()->toString();
}

std::shared_ptr<Timestamp> QueueKey::getCurrentTimestamp() const {
    return currentTimestamp;
}

bool QueueKey::operator<(const std::shared_ptr<QueueKey>& other) const {
    return COMPARATOR(shared_from_this(), other);
}

