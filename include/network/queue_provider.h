#ifndef NETWORK_QUEUE_PROVIDER_H
#define NETWORK_QUEUE_PROVIDER_H

#include "network/queue.h"

class QueueProvider {
public:
    virtual ~QueueProvider() = default;
    virtual Queue* getQueue() = 0;
};

#endif // NETWORK_QUEUE_PROVIDER_H 