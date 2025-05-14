#ifndef NETWORK_ELEMENT_H
#define NETWORK_ELEMENT_H

#include "fields/queue_provider.h"

class Element : public QueueProvider {
public:
    virtual ~Element() = default;
    virtual long getCreated() = 0;
    virtual long getFired() = 0;
    // inherited from QueueProvider
    virtual Queue* getQueue() const = 0;
};

#endif // NETWORK_ELEMENT_H 