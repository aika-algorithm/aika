
#include "fields/step.h"
#include "fields/field_update.h"
#include "fields/queue_interceptor.h"


QueueInterceptor::QueueInterceptor(Queue* q,
                                   Field* f,
                                   ProcessingPhase& phase,
                                   bool isNextRound)
    : queue(q), field(f), phase(phase), isNextRound(isNextRound), step(nullptr) {}

FieldUpdate* QueueInterceptor::getStep() const {
    return step;
}

Field* QueueInterceptor::getField() const {
    return field;
}

bool QueueInterceptor::getIsNextRound() const {
    return isNextRound;
}

FieldUpdate* QueueInterceptor::getOrCreateStep() {
    if (!step) {
        step = new FieldUpdate(phase, this);
    }
    return step;
}

void QueueInterceptor::receiveUpdate(double u, bool replaceUpdate) {
    auto s = getOrCreateStep();
    s->updateDelta(u, replaceUpdate);

    if (u != 0.0 && !s->getIsQueued()) {
        if (!Step::add(s)) {
            process(s);
        }
    }
}

void QueueInterceptor::process(FieldUpdate* s) {
    step = nullptr;
    field->triggerUpdate(s->getDelta());
}

Queue* QueueInterceptor::getQueue() const {
    return queue;
}
