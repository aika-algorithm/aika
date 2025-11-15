/**
 * @file queue_interceptor.cpp
 * @brief Implements the QueueInterceptor class functionality.
 * 
 * This file contains the implementation of the QueueInterceptor class,
 * which manages the integration of field updates with the event queue system.
 * It handles the creation and processing of field update steps, ensuring
 * proper temporal ordering of updates in the field graph.
 */

#include "fields/step.h"
#include "fields/field_update.h"
#include "fields/queue_interceptor.h"
#include "fields/field.h"

/**
 * @brief Constructs a new QueueInterceptor instance.
 * 
 * Initializes the interceptor with the given queue, field, processing phase,
 * and next-round processing flag.
 * 
 * @param q The event queue to use for updates
 * @param f The field to intercept updates for
 * @param phase The processing phase for updates
 * @param isNextRound Whether to process updates in the next round
 */
QueueInterceptor::QueueInterceptor(Queue* q,
                                   Field* f,
                                   ProcessingPhase& phase,
                                   bool isNextRound)
    : queue(q), field(f), phase(phase), isNextRound(isNextRound), step(nullptr) {}

/**
 * @brief Gets the current field update step.
 * 
 * @return The current step, or nullptr if none exists
 */
FieldUpdate* QueueInterceptor::getStep() const {
    return step;
}

/**
 * @brief Gets the field being updated.
 * 
 * @return The field
 */
Field* QueueInterceptor::getField() const {
    return field;
}

/**
 * @brief Gets whether updates should be processed in the next round.
 * 
 * @return true if updates should be processed in the next round
 */
bool QueueInterceptor::getIsNextRound() const {
    return isNextRound;
}

/**
 * @brief Gets the current step or creates a new one if none exists.
 * 
 * @return The current or newly created step
 */
FieldUpdate* QueueInterceptor::getOrCreateStep() {
    if (!step) {
        step = new FieldUpdate(phase, this);
    }
    return step;
}

/**
 * @brief Receives an update for the field.
 * 
 * Creates or updates a FieldUpdate step with the new update value.
 * If the update is non-zero and the step is not already queued,
 * it either adds the step to the queue or processes it immediately.
 * 
 * @param u The update value
 * @param replaceUpdate Whether to replace the existing update
 */
void QueueInterceptor::receiveUpdate(double u, bool replaceUpdate) {
    auto s = getOrCreateStep();
    s->updateDelta(u, replaceUpdate);

    if (u != 0.0 && !s->getIsQueued()) {
        if (!Step::add(s)) {
            process(s);
        }
    }
}

/**
 * @brief Processes a field update step.
 * 
 * Clears the current step and triggers the update on the field.
 * 
 * @param s The step to process
 */
void QueueInterceptor::process(FieldUpdate* s) {
    step = nullptr;
    field->triggerUpdate(s->getDelta());
}

/**
 * @brief Gets the event queue.
 * 
 * @return The event queue
 */
Queue* QueueInterceptor::getQueue() const {
    return queue;
}
