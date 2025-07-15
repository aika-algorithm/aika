/**
 * @file field_update.h
 * @brief Defines the FieldUpdate class, which represents field update steps in the event queue.
 * 
 * The FieldUpdate class encapsulates field value changes as steps in the event queue,
 * ensuring proper temporal ordering of updates and maintaining the field graph's state.
 */

#ifndef FIELD_UPDATE_H
#define FIELD_UPDATE_H

#include <string>

#include "fields/queue.h"
#include "fields/object.h"
#include "fields/step.h"

class QueueInterceptor;

/**
 * @class FieldUpdate
 * @brief Represents a field update step in the event queue.
 * 
 * FieldUpdate instances are created by QueueInterceptor to manage field value changes
 * in the event queue. Each update step:
 * - Tracks the delta (change) in field value
 * - Maintains a reference to its QueueInterceptor
 * - Handles queuing and processing of updates
 * - Ensures updates are processed in the correct order
 */
class FieldUpdate : public Step {
private:
    QueueInterceptor* interceptor;  ///< The QueueInterceptor managing this update
    ProcessingPhase& phase;
    int sortValue = INT_MAX;
    double delta = 0.0;

    void updateSortValue(double delta);

public:
    /**
     * @brief Constructs a new FieldUpdate step
     * 
     * @param phase The processing phase for this update
     * @param interceptor The QueueInterceptor managing this update
     */
    FieldUpdate(ProcessingPhase& p, QueueInterceptor* qf);

    bool incrementRound();
    void updateDelta(double delta, bool replaceUpdate);
    void reset();

    int getSortValue() const;
    double getDelta() const;
    Queue* getQueue() const;
    void createQueueKey(long timestamp, int round);
    void process();

    ProcessingPhase& getPhase() const;

    QueueInterceptor* getInterceptor() const;
    std::string toShortString() const;
    std::string toString() const;
};

#endif //FIELD_UPDATE_H
