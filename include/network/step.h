#ifndef NETWORK_STEP_H
#define NETWORK_STEP_H

#include "network/queue.h"
#include "network/timestamp.h"
#include <string>

class Step {
public:
    virtual ~Step() = default;
    virtual void createQueueKey(Timestamp timestamp, int round) = 0;
    virtual void process() = 0;
    virtual std::string toString() const = 0;
};

#endif // NETWORK_STEP_H 