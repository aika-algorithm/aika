/**
 * @file queue_interceptor.h
 * @brief Defines the QueueInterceptor class, which manages field updates in the event queue.
 * 
 * The QueueInterceptor class is a key component of AIKA's event-driven processing system.
 * It intercepts field updates and manages their queuing and processing through the event queue,
 * ensuring proper temporal ordering of updates and maintaining the field graph's state.
 */

#ifndef QUEUE_INTERCEPTOR_H
#define QUEUE_INTERCEPTOR_H


class FieldUpdate;
class Field;
class Queue;
class ProcessingPhase;

/**
 * @class QueueInterceptor
 * @brief Manages the integration of field updates with the event queue system.
 * 
 * The QueueInterceptor class:
 * - Intercepts field updates and creates corresponding FieldUpdate steps
 * - Manages the queuing of updates in the correct processing phase
 * - Ensures updates are processed in the proper temporal order
 * - Handles both immediate and next-round processing
 * 
 * This class is essential for implementing the event-driven nature of the field graph,
 * where updates are processed asynchronously through a time-ordered queue.
 */
class QueueInterceptor {
private:
    ProcessingPhase& phase;    ///< The processing phase for updates
    FieldUpdate* step;         ///< The current field update step
    Field* field;              ///< The field being updated
    Queue* queue;              ///< The event queue
    const bool isNextRound;    ///< Whether to process in the next round

public:
    /**
     * @brief Constructs a new QueueInterceptor
     * 
     * @param q The event queue to use
     * @param f The field to intercept updates for
     * @param phase The processing phase for updates
     * @param isNextRound Whether to process updates in the next round
     */
    QueueInterceptor(Queue* q,
                     Field* f,
                     ProcessingPhase& phase,
                     bool isNextRound);

    /**
     * @brief Gets the current field update step
     * 
     * @return The current step, or nullptr if none exists
     */
    FieldUpdate* getStep() const;

    /**
     * @brief Gets the field being updated
     * 
     * @return The field
     */
    Field* getField() const;

    /**
     * @brief Gets whether updates should be processed in the next round
     * 
     * @return true if updates should be processed in the next round
     */
    bool getIsNextRound() const;

    /**
     * @brief Receives an update for the field
     * 
     * Creates or updates a FieldUpdate step with the new update value.
     * 
     * @param u The update value
     * @param replaceUpdate Whether to replace the existing update
     */
    void receiveUpdate(double u, bool replaceUpdate);

    /**
     * @brief Processes a field update step
     * 
     * @param s The step to process
     */
    void process(FieldUpdate* s);

    /**
     * @brief Gets the event queue
     * 
     * @return The event queue
     */
    Queue* getQueue() const;

private:
    /**
     * @brief Gets the current step or creates a new one
     * 
     * @return The current or newly created step
     */
    FieldUpdate* getOrCreateStep();
};

#endif //QUEUE_INTERCEPTOR_H
