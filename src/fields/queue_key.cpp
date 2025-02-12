#include "fields/queue_key.h"
#include "fields/queue.h"
#include <sstream>

const std::function<bool(const QueueKey*, const QueueKey*)> QueueKey::COMPARATOR =
    [](const QueueKey* k1, const QueueKey* k2) -> bool {
        if (k1->getRound() != k2->getRound())
            return k1->getRound() < k2->getRound();
        if (k1->getPhase()->rank() != k2->getPhase()->rank())
            return k1->getPhase()->rank() < k2->getPhase()->rank();
        if (k1->getCurrentTimestamp() != k2->getCurrentTimestamp())
            return k1->getCurrentTimestamp() < k2->getCurrentTimestamp();
        return false;
};

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
    return getPhase()->toString() + "-" + getPhase()->toString();
}

long QueueKey::getCurrentTimestamp() const {
    return currentTimestamp;
}

bool QueueKey::operator<(const QueueKey* other) const {
    return COMPARATOR(this, other);
}

#include "FieldQueueKey.h"
#include <sstream>
#include <limits>  // For Integer.MAX_VALUE equivalent

// Constructor
FieldQueueKey::FieldQueueKey(int round, ProcessingPhase phase, int sortValue, Timestamp currentTimestamp)
    : QueueKey(round, phase, currentTimestamp), sortValue(sortValue) {}

// Getter for sortValue
int FieldQueueKey::getSortValue() const {
    return sortValue;
}

// Helper method for getting sort value as string
std::string FieldQueueKey::getSortValueAsString() const {
    return sortValue == std::numeric_limits<int>::max() ? "MAX" : std::to_string(sortValue);
}

// Comparator function (for sorting)
bool FieldQueueKey::compare(const FieldQueueKey& lhs, const FieldQueueKey& rhs) {
    return lhs.sortValue > rhs.sortValue;  // Reversing the order (-k.sortValue in Java)
}

// compareTo implementation
int FieldQueueKey::compareTo(const QueueKey& qk) const {
    // We cast to FieldQueueKey because compareTo only works between the same type
    const FieldQueueKey& fqk = static_cast<const FieldQueueKey&>(qk);
    if (this->sortValue > fqk.sortValue) return 1;
    if (this->sortValue < fqk.sortValue) return -1;
    return 0;
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


