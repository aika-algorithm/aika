/**
 * @file step.h
 * @brief Defines the Step class, which is the base class for all steps in the event queue.
 * 
 * The Step class provides the foundation for all steps in the event queue system,
 * defining common functionality for queuing, processing, and managing step state.
 */

#ifndef STEP_H
#define STEP_H

#include <memory>

#include "queue_provider.h"
#include "fields/queue_key.h"

class Queue;


/**
 * @class Step
 * @brief Base class for all steps in the event queue.
 * 
 * The Step class provides the core functionality for steps in the event queue:
 * - Queue key management for ordering
 * - Round tracking for multi-round processing
 * - Phase management for step categorization
 * - Abstract processing interface
 * 
 * All concrete step types (like FieldUpdate) inherit from this base class
 * to ensure consistent behavior in the event queue system.
 */
class Step : public QueueProvider {
protected:
    bool isQueued;
    QueueKey* queueKey;

public:
    Step() : isQueued(false), queueKey(nullptr) {}

    void setIsQueued(bool queued);
    bool getIsQueued() const;
    QueueKey* getQueueKey() const;

    virtual Queue* getQueue() const = 0;
    virtual bool incrementRound();
    virtual void createQueueKey(long timestamp, int round) = 0;
    virtual void process() = 0;
    virtual const ProcessingPhase& getPhase() const = 0;
    static bool add(Step* s);

};

#endif // STEP_H

