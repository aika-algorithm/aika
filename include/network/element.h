#ifndef NETWORK_ELEMENT_H
#define NETWORK_ELEMENT_H

#include "network/timestamp.h"

class Element {
public:
    virtual ~Element() = default;
    virtual Timestamp getCreated() = 0;
    virtual Timestamp getFired() = 0;
};

#endif // NETWORK_ELEMENT_H 