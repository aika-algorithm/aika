/**
 * @file field.h
 * @brief Defines the Field class, which represents runtime instances of field definitions.
 * 
 * The Field class is the runtime representation of a field definition in the field graph.
 * It manages the current and updated values of a field, handles update propagation,
 * and integrates with the event queue system for asynchronous processing.
 */

#ifndef FIELD_H
#define FIELD_H

#include <string>

#include "fields/field_definition.h"
#include "fields/obj.h"
#include "fields/queue_interceptor.h"


class Queue;
class ProcessingPhase;


/**
 * @class Field
 * @brief Runtime instance of a field definition in the field graph.
 * 
 * Fields are the computational units that perform actual calculations in the field graph.
 * Each field:
 * - Maintains current and updated values
 * - Propagates updates to connected fields
 * - Integrates with the event queue for asynchronous processing
 * - Associates with an object in the object graph
 * 
 * The field graph's event-driven nature is implemented through the QueueInterceptor,
 * which manages when and how updates are processed.
 */
class Field {
private:
    FieldDefinition* fieldDefinition;  ///< The field definition this field is an instance of
    int id;                           ///< Unique identifier for this field
    Obj* object;                      ///< The object this field is associated with
    double value;                     ///< Current value of the field
    double updatedValue;              ///< Updated value during processing
    bool withinUpdate;                ///< Flag indicating if field is being updated
    QueueInterceptor* interceptor;    ///< Manages event queue integration

public:
    /**
     * @brief Constructs a new Field
     * 
     * @param obj The object this field is associated with
     * @param fd The field definition this field is an instance of
     * @param id The unique identifier for this field
     */
    Field(Obj* obj, FieldDefinition* fd, int id);

    /**
     * @brief Gets the field's unique identifier
     * 
     * @return The field ID
     */
    int getId() const;

    /**
     * @brief Checks if the field is currently being updated
     * 
     * @return true if the field is within an update operation
     */
    bool isWithinUpdate();

    /**
     * @brief Gets the current value of the field
     * 
     * @return The current value
     */
    double getValue();

    /**
     * @brief Gets the updated value of the field
     * 
     * @return The updated value
     */
    double getUpdatedValue();

    /**
     * @brief Gets the object this field is associated with
     * 
     * @return The associated object
     */
    Obj* getObject();

    /**
     * @brief Sets the field to be queued for processing
     * 
     * @param q The queue to use
     * @param phase The processing phase
     * @param isNextRound Whether to process in the next round
     * @return This field for method chaining
     */
    Field& setQueued(Queue* q, ProcessingPhase& phase, bool isNextRound);

    /**
     * @brief Gets the field definition this field is an instance of
     * 
     * @return The field definition
     */
    FieldDefinition* getFieldDefinition();

    /**
     * @brief Gets the tolerance threshold for this field
     * 
     * @return The tolerance threshold
     */
    double getTolerance() const;

    /**
     * @brief Gets the name of this field
     * 
     * @return The field name
     */
    std::string getName() const;

    /**
     * @brief Gets the queue interceptor for this field
     * 
     * @return The queue interceptor
     */
    QueueInterceptor* getInterceptor();

    /**
     * @brief Sets the queue interceptor for this field
     * 
     * @param interceptor The queue interceptor to set
     */
    void setInterceptor(QueueInterceptor* interceptor);

    /**
     * @brief Sets the current value of the field
     * 
     * @param v The value to set
     */
    void setValue(double v);

    /**
     * @brief Triggers an update to the field's value
     * 
     * @param u The update value
     */
    void triggerUpdate(double u);

    /**
     * @brief Gets the current update value
     * 
     * @return The update value
     */
    double getUpdate() const;

    /**
     * @brief Propagates the current update to connected fields
     */
    void propagateUpdate();

    /**
     * @brief Receives an update from another field
     * 
     * @param u The update value
     */
    void receiveUpdate(double u);

    /**
     * @brief Converts the field to a string representation
     * 
     * @return String representation
     */
    std::string toString() const;

    /**
     * @brief Gets a string representation of the field's value
     * 
     * @return Value string representation
     */
    std::string getValueString() const;

    /**
     * @brief Checks if a field's value is considered true
     * 
     * @param f The field to check
     * @return true if the field's value is considered true
     */
    static bool isTrue(Field* f);

    /**
     * @brief Checks if a field's value is considered true
     * 
     * @param f The field to check
     * @param updatedValue Whether to use the updated value
     * @return true if the field's value is considered true
     */
    static bool isTrue(Field* f, bool updatedValue);

    /**
     * @brief Checks if a value is considered true
     * 
     * @param v The value to check
     * @return true if the value is considered true
     */
    static bool isTrue(double v);
};

#endif // FIELD_H
