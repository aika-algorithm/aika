
#include "fields/queue_interceptor.h"
#include "fields/field_update.h"
#include "fields/step.h"

QueueInterceptor::QueueInterceptor(std::shared_ptr<Queue> q,
                                   std::shared_ptr<Field> f,
                                   std::shared_ptr<ProcessingPhase> phase,
                                   bool isNextRound)
    : queue(q), field(f), phase(phase), isNextRound(isNextRound), step(nullptr) {}

std::shared_ptr<FieldUpdate> QueueInterceptor::getStep() const {
    return step;
}

std::shared_ptr<Field> QueueInterceptor::getField() const {
    return field;
}

bool QueueInterceptor::isNextRound() const {
    return isNextRound;
}

std::shared_ptr<FieldUpdate> QueueInterceptor::getOrCreateStep() {
    if (!step) {
        step = std::make_shared<FieldUpdate>(phase, shared_from_this());
    }
    return step;
}

void QueueInterceptor::receiveUpdate(double u, bool replaceUpdate) {
    auto s = getOrCreateStep();
    s->updateDelta(u, replaceUpdate);

    if (u != 0.0 && !s->isQueued()) {
        if (!Step::add(s)) {
            process(s);
        }
    }
}

void QueueInterceptor::process(std::shared_ptr<FieldUpdate> s) {
    step = nullptr;
    field->triggerUpdate(s->getDelta());
}

std::shared_ptr<Queue> QueueInterceptor::getQueue() const {
    return queue;
}
