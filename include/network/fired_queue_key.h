#ifndef NETWORK_FIRED_QUEUE_KEY_H
#define NETWORK_FIRED_QUEUE_KEY_H

#include "network/queue_key.h"
#include "network/element.h"
#include "network/processing_phase.h"
#include "network/timestamp.h"
#include <string>
#include <functional>

class FiredQueueKey : public QueueKey {
public:
    FiredQueueKey(int round, ProcessingPhase phase, Element* element, Timestamp currentTimestamp);

    Timestamp getFired() const;
    Timestamp getCreated() const;
    std::string toString() const override;
    int compareTo(QueueKey* qk) const override;

private:
    static const std::function<int(const FiredQueueKey*, const FiredQueueKey*)> COMPARATOR;

    Timestamp created;
    Timestamp fired;
};

#endif // NETWORK_FIRED_QUEUE_KEY_H 