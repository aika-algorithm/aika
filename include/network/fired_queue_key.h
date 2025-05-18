#ifndef NETWORK_FIRED_QUEUE_KEY_H
#define NETWORK_FIRED_QUEUE_KEY_H

#include "fields/queue_key.h"
#include "network/element.h"
#include <string>
#include <functional>

class FiredQueueKey : public QueueKey {
public:
    FiredQueueKey(int round, const ProcessingPhase& phase, Element* element, long currentTimestamp);

    long getFired() const;
    long getCreated() const;
    std::string toString() const;
    int compareTo(QueueKey* qk) const;

private:
    static const std::function<int(const FiredQueueKey*, const FiredQueueKey*)> COMPARATOR;

    long created;
    long fired;
};

#endif // NETWORK_FIRED_QUEUE_KEY_H 