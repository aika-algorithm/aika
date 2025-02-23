

#include "fields/field_update.h"
#include "fields/utils.h"

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

