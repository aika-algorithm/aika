#include "fields/queue_key.h"
#include "fields/queue.h"
#include <sstream>
#include <limits>  // For Integer.MAX_VALUE equivalent


QueueKey::QueueKey(int round, ProcessingPhase& phase, long currentTimestamp)
    : round(round), phase(phase), currentTimestamp(currentTimestamp) {}

int QueueKey::getRound() const {
    return round;
}

std::string QueueKey::getRoundStr() const {
    return std::to_string(getRound());
}

ProcessingPhase& QueueKey::getPhase() const {
    return phase;
}

std::string QueueKey::getPhaseStr() const {
  std::ostringstream oss;
    oss << getPhase().rank();
    return oss.str();
}

long QueueKey::getCurrentTimestamp() const {
    return currentTimestamp;
}

bool QueueKey::operator<(const QueueKey& other) const {
    if (getRound() != other.getRound())
        return getRound() < other.getRound();
    if (getPhase().rank() != other.getPhase().rank())
        return getPhase().rank() < other.getPhase().rank();
    if (getCurrentTimestamp() != other.getCurrentTimestamp())
        return getCurrentTimestamp() < other.getCurrentTimestamp();
    return false;
}

// Constructor
FieldQueueKey::FieldQueueKey(int round, ProcessingPhase& phase, int sortValue, long currentTimestamp)
    : QueueKey(round, phase, currentTimestamp), sortValue(sortValue) {}

// Getter for sortValue
int FieldQueueKey::getSortValue() const {
    return sortValue;
}

// Helper method for getting sort value as string
std::string FieldQueueKey::getSortValueAsString() const {
    return sortValue == std::numeric_limits<int>::max() ? "MAX" : std::to_string(sortValue);
}

bool FieldQueueKey::operator<(const QueueKey& other) const {
    const FieldQueueKey& fqk = static_cast<const FieldQueueKey&>(other);
    return this->sortValue < fqk.sortValue;
}

// toString implementation
std::string FieldQueueKey::toString() const {
    std::ostringstream oss;
    oss << "[r:" << getRoundStr()
        << ", p:" << getPhaseStr()
        << ", sv:" << getSortValueAsString()
        << ", ts:" << getCurrentTimestamp()
        << "]";
    return oss.str();
}


