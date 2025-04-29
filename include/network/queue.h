#ifndef NETWORK_QUEUE_H
#define NETWORK_QUEUE_H

#include <vector>
#include "network/step.h"

class Queue {
public:
    Queue();
    virtual ~Queue();

    void addStep(Step* step);
    void processSteps();

private:
    std::vector<Step*> steps;
};

#endif // NETWORK_QUEUE_H 