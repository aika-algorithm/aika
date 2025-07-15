/**
 * @file field.cpp
 * @brief Implementation of the Field class, which represents runtime instances of field definitions.
 * 
 * This file implements the functionality defined in field.h, providing the concrete
 * implementation of fields that perform actual calculations in the field graph.
 * The implementation handles value management, update propagation, and integration
 * with the event queue system.
 */

#include <stdexcept>
#include <iostream>
#include <sstream>

#include "fields/field.h"
#include "fields/object.h"
#include "fields/field_definition.h"
#include "fields/queue_interceptor.h"

/**
 * @brief Constructs a new Field
 * 
 * Initializes a field with its associated object, field definition, and ID.
 * Sets initial values to 0.0 and flags to false.
 * 
 * @param obj The object this field is associated with
 * @param fd The field definition this field is an instance of
 * @param id The unique identifier for this field
 */
Field::Field(Object* obj, FieldDefinition* fd, int id)
    : object(obj), fieldDefinition(fd), id(id), value(0.0), updatedValue(0.0), withinUpdate(false), interceptor(nullptr) {}

/**
 * @brief Gets the field's unique identifier
 * 
 * @return The field ID
 */
int Field::getId() const {
    return id;
}

/**
 * @brief Checks if the field is currently being updated
 * 
 * @return true if the field is within an update operation
 */
bool Field::isWithinUpdate() {
    return withinUpdate;
}

/**
 * @brief Gets the current value of the field
 * 
 * @return The current value
 */
double Field::getValue() {
    return value;
}

/**
 * @brief Gets the updated value of the field
 * 
 * Returns the updated value if within an update operation,
 * otherwise returns the current value.
 * 
 * @return The updated value or current value
 */
double Field::getUpdatedValue() {
    return withinUpdate ? updatedValue : value;
}

/**
 * @brief Gets the object this field is associated with
 * 
 * @return The associated object
 */
Object* Field::getObject() {
    return object;
}

/**
 * @brief Sets the field to be queued for processing
 * 
 * Creates a new QueueInterceptor to manage the field's integration
 * with the event queue system.
 * 
 * @param q The queue to use
 * @param phase The processing phase
 * @param isNextRound Whether to process in the next round
 * @return This field for method chaining
 */
Field& Field::setQueued(Queue* q, ProcessingPhase& phase, bool isNextRound) {
    interceptor = new QueueInterceptor(q, this, phase, isNextRound);
    return *this;
}

/**
 * @brief Gets the field definition this field is an instance of
 * 
 * @return The field definition
 */
FieldDefinition* Field::getFieldDefinition() {
    return fieldDefinition;
}

/**
 * @brief Gets the tolerance threshold for this field
 * 
 * @return The tolerance threshold, or 0.0 if not set
 */
double Field::getTolerance() const {
    return fieldDefinition->getTolerance().value_or(0.0);
}

/**
 * @brief Gets the name of this field
 * 
 * @return The field name
 */
std::string Field::getName() const {
    return fieldDefinition->getName();
}

/**
 * @brief Gets the queue interceptor for this field
 * 
 * @return The queue interceptor
 */
QueueInterceptor* Field::getInterceptor() {
    return interceptor;
}

/**
 * @brief Sets the queue interceptor for this field
 * 
 * @param interceptor The queue interceptor to set
 */
void Field::setInterceptor(QueueInterceptor* interceptor) {
    this->interceptor = interceptor;
}

/**
 * @brief Sets the current value of the field
 * 
 * Sets the updated value and propagates the change to connected fields.
 * 
 * @param v The value to set
 */
void Field::setValue(double v) {
    withinUpdate = true;
    updatedValue = v;
    propagateUpdate();
}

/**
 * @brief Triggers an update to the field's value
 * 
 * Adds the update to the current value and propagates the change.
 * 
 * @param u The update value
 */
void Field::triggerUpdate(double u) {
//    if (ToleranceUtils::belowTolerance(getTolerance(), u)) {
//        return;
//    }

    withinUpdate = true;
    updatedValue = value + u;
    propagateUpdate();
}

/**
 * @brief Propagates the current update to connected fields
 * 
 * Follows the output links in the flattened type to propagate
 * the update to connected fields, then finalizes the update.
 */
void Field::propagateUpdate() {
    object->getType()->getFlattenedTypeOutputSide()->followLinks(this);
    value = updatedValue;
    withinUpdate = false;
}

/**
 * @brief Gets the current update value
 * 
 * @return The difference between updated and current values
 */
double Field::getUpdate() const {
    return updatedValue - value;
}

/**
 * @brief Receives an update from another field
 * 
 * If the field has an interceptor, passes the update to it.
 * Otherwise, triggers the update directly.
 * 
 * @param u The update value
 * @throws std::logic_error if the field is already being updated
 */
void Field::receiveUpdate(double u) {
    if (interceptor != nullptr) {
        interceptor->receiveUpdate(u, false);
        return;
    }

    if (withinUpdate) {
        throw std::logic_error("Field is already within update.");
    }
    triggerUpdate(u);
}

/**
 * @brief Converts the field to a string representation
 * 
 * @return String representation in the format "name: value"
 */
std::string Field::toString() const {
    return getName() + ": " + getValueString();
}

/**
 * @brief Gets a string representation of the field's value
 * 
 * @return Value string representation
 */
std::string Field::getValueString() const {
    return std::to_string(value);
}