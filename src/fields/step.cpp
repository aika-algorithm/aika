#include <cassert>
#include "fields/queue_key.h"
#include "fields/queue.h"
#include <iostream>
#include <cassert>


void Step::setIsQueued(bool queued) {
    isQueued = queued;
}

bool Step::getIsQueued() const {
    return isQueued;
}

QueueKey* Step::getQueueKey() const {
    return queueKey;
}

bool Step::incrementRound() {
    return false;
}

bool Step::add(Step* s) {
    Queue* q = s->getQueue();
    if (!q)
        return false;

    q->addStep(s);
    return true;
}

