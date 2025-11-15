/**
 * @file field_update.cpp
 * @brief Implements the FieldUpdate class functionality.
 * 
 * This file contains the implementation of the FieldUpdate class,
 * which manages field value changes as steps in the event queue.
 * It handles the creation, queuing, and processing of field updates,
 * ensuring proper temporal ordering of changes in the field graph.
 */

#include <climits>
#include <sstream>

#include "fields/field_update.h"
#include "fields/queue_interceptor.h"
#include "fields/field.h"
#include "fields/utils.h"

/**
 * @brief Constructs a new FieldUpdate step
 * 
 * Initializes the update step with the given phase and interceptor.
 * Sets initial values for delta and queued state.
 * 
 * @param phase The processing phase for this update
 * @param interceptor The QueueInterceptor managing this update
 */
FieldUpdate::FieldUpdate(ProcessingPhase& p, QueueInterceptor* qf)
    : phase(p), interceptor(qf) {}

bool FieldUpdate::incrementRound() {
    return interceptor->getIsNextRound();
}

void FieldUpdate::updateSortValue(double delta) {
    int newSortValue = convertSortValue(delta);
    if (std::abs(sortValue - newSortValue) == 0) {
        return;
    }

    if (getIsQueued()) {
        auto q = getQueue();
        q->removeStep(this);
        sortValue = newSortValue;
        q->addStep(this);
    } else {
        sortValue = newSortValue;
    }
}

int FieldUpdate::getSortValue() const {
    return sortValue;
}

void FieldUpdate::updateDelta(double delta, bool replaceUpdate) {
    if (replaceUpdate)
        this->delta = 0;

    this->delta += delta;

    updateSortValue(std::abs(this->delta));
}

void FieldUpdate::reset() {
    delta = 0.0;
}

double FieldUpdate::getDelta() const {
    return delta;
}

Queue* FieldUpdate::getQueue() const {
    return interceptor->getQueue();
}

void FieldUpdate::createQueueKey(long timestamp, int round) {
    queueKey = new FieldQueueKey(round, getPhase(), sortValue, timestamp);
}

void FieldUpdate::process() {
    interceptor->process(this);
}

ProcessingPhase& FieldUpdate::getPhase() const {
    return phase;
}

QueueInterceptor* FieldUpdate::getInterceptor() const {
    return interceptor;
}

std::string FieldUpdate::toShortString() const {
    return " Round:" + std::to_string(getQueueKey()->getRound()) +
           " Delta:" + std::to_string(delta);
}

std::string FieldUpdate::toString() const {
    return interceptor->getField()->toString() + " Delta:" + std::to_string(delta) +
           " Field: " + interceptor->getField()->toString() +
           " Ref:" + interceptor->getField()->getObject()->toString();
}

