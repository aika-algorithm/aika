/**
 * @file step.cpp
 * @brief Implements the Step class functionality.
 * 
 * This file contains the implementation of the Step class,
 * which provides the foundation for all steps in the event queue system.
 * It handles queue key management, round tracking, and basic step functionality.
 */

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

