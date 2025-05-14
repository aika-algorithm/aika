#ifndef NETWORK_ELEMENT_H
#define NETWORK_ELEMENT_H

#include "network/timestamp.h"
#include "fields/queue_provider.h"

class Element : public QueueProvider {
public:
    virtual ~Element() = default;
    virtual Timestamp getCreated() = 0;
    virtual Timestamp getFired() = 0;
};

#endif // NETWORK_ELEMENT_H 