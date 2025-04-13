/**
 * @file field_definition.cpp
 * @brief Implementation of the FieldDefinition class, which forms the core of the field graph.
 * 
 * This file implements the functionality defined in field_definition.h, providing the concrete
 * implementation of field definitions that form the nodes of the field graph. The implementation
 * handles field initialization, update propagation, and connection management between fields.
 */

#include <iostream>
#include <sstream>

#include "fields/type.h"
#include "fields/field.h"
#include "fields/field_definition.h"
#include "fields/field_link_definition.h"
#include "fields/obj.h"

/**
 * @brief Constructs a new FieldDefinition
 * 
 * Initializes a field definition with its associated object type, name, number of arguments,
 * and tolerance threshold. The field definition is registered with its object type.
 * 
 * @param objectType The type in the object graph this field is associated with
 * @param name The name of the field definition
 * @param numArgs The number of arguments this field expects
 * @param tolerance The tolerance threshold for updates
 */
FieldDefinition::FieldDefinition(Type* objectType, const std::string& name, int numArgs, double tolerance)
    : objectType(objectType), name(name), isNextRound(false) {
    this->tolerance = tolerance;
    objectType->setFieldDefinition(this);

    if (numArgs > 0) {
        inputs.reserve(numArgs);
    }
}

/**
 * @brief Sets the unique identifier for this field definition
 * 
 * @param fieldId The identifier to set
 */
void FieldDefinition::setFieldId(int fieldId) {
    this->fieldId = fieldId;
}

/**
 * @brief Transmits an update to a target field through a field link
 * 
 * This default implementation simply passes the update to receiveUpdate.
 * Subclasses may override this to implement custom update propagation logic.
 * 
 * @param targetField The field receiving the update
 * @param fieldLink The link through which the update is transmitted
 * @param update The value of the update
 */
void FieldDefinition::transmit(Field* targetField, FieldLinkDefinition* fieldLink, double update) {
    receiveUpdate(targetField, update);
}

/**
 * @brief Receives an update from another field
 * 
 * Checks if the field's object is of the correct type and if the update exceeds
 * the tolerance threshold before propagating the update.
 * 
 * @param field The field sending the update
 * @param update The value of the update
 */
void FieldDefinition::receiveUpdate(Field* field, double update) {
    if (!field->getObject()->isInstanceOf(objectType)) return;

//    if (ToleranceUtils::belowTolerance(getTolerance().value_or(0.0), update)) return;

    field->receiveUpdate(update);
}

/**
 * @brief Gets the parent field definition
 * 
 * @return The parent field definition
 */
FieldDefinition* FieldDefinition::getParent() const {
    return parent;
}

/**
 * @brief Sets the parent field definition
 * 
 * Establishes a parent-child relationship between field definitions.
 * 
 * @param parent The parent to set
 * @return This field definition for method chaining
 */
FieldDefinition* FieldDefinition::setParent(FieldDefinition* parent) {
    this->parent = parent;
    parent->children.push_back(this);
    return this;
}

/**
 * @brief Gets all child field definitions
 * 
 * @return Vector of child field definitions
 */
std::vector<FieldDefinition*> FieldDefinition::getChildren() const {
    return children;
}

/**
 * @brief Checks if this field is required given a set of field definitions
 * 
 * A field is required if it is the most specific definition in the inheritance
 * hierarchy that is present in the given set.
 * 
 * @param fieldDefs The set of field definitions to check against
 * @return true if this field is required, false otherwise
 */
bool FieldDefinition::isFieldRequired(const std::set<FieldDefinition*>& fieldDefs) {
    return resolveInheritedFieldDefinition(fieldDefs) == this;
}

/**
 * @brief Resolves the inherited field definition from a set of field definitions
 * 
 * Recursively searches through the inheritance hierarchy to find the most specific
 * field definition that is present in the given set.
 * 
 * @param fieldDefs The set of field definitions to resolve from
 * @return The resolved field definition
 */
FieldDefinition* FieldDefinition::resolveInheritedFieldDefinition(const std::set<FieldDefinition*>& fieldDefs) {
    for (const auto& child : children) {
        if (fieldDefs.find(child) != fieldDefs.end()) {
            return child->resolveInheritedFieldDefinition(fieldDefs);
        }
    }
    return this;
}

/**
 * @brief Initializes a field instance based on this definition
 * 
 * Sets up the field's connections by following the flattened type's input links.
 * 
 * @param field The field to initialize
 */
void FieldDefinition::initializeField(Field* field) {
    field->getObject()->getType()->getFlattenedTypeInputSide()->followLinks(field);
}

/**
 * @brief Adds an input field link
 * 
 * Adds a field link to the inputs vector, handling both positional and non-positional arguments.
 * 
 * @param fl The field link to add
 */
void FieldDefinition::addInput(FieldLinkDefinition* fl) {
    if(fl->getArgument() >= 0) 
        inputs[fl->getArgument()] = fl;
    else
        inputs.push_back(fl);    
}

/**
 * @brief Gets all input field links
 * 
 * @return Vector of input field links
 */
std::vector<FieldLinkDefinition*> FieldDefinition::getInputs() {
    return inputs;
}

/**
 * @brief Adds an output field link
 * 
 * @param fl The field link to add
 */
void FieldDefinition::addOutput(FieldLinkDefinition* fl) {
    outputs.push_back(fl);
}

/**
 * @brief Gets all output field links
 * 
 * @return Vector of output field links
 */
std::vector<FieldLinkDefinition*> FieldDefinition::getOutputs() {
    return outputs;
}

/**
 * @brief Creates an input connection with a relation
 * 
 * Establishes a link between an input field and this field using the specified relation.
 * 
 * @param relation The relation to use
 * @param input The input field definition
 * @param arg The argument index
 * @return This field definition for method chaining
 */
FieldDefinition& FieldDefinition::input(Relation& relation, FieldDefinition& input, int arg) {
    FieldLinkDefinition::link(&input, this, &relation, arg);
//    assert(relation || objectType->isInstanceOf(output->getObjectType()) || output->getObjectType()->isInstanceOf(objectType));
    return *this;
}

/**
 * @brief Creates an output connection with a relation
 * 
 * Establishes a link between this field and an output field using the specified relation.
 * 
 * @param relation The relation to use
 * @param output The output field definition
 * @param arg The argument index
 * @return This field definition for method chaining
 */
FieldDefinition& FieldDefinition::output(Relation& relation, FieldDefinition& output, int arg) {
    FieldLinkDefinition::link(this, &output, &relation, arg);
//    assert(relation || objectType->isInstanceOf(output->getObjectType()) || output->getObjectType()->isInstanceOf(objectType));
    return *this;
}

/**
 * @brief Sets the name of this field definition
 * 
 * @param name The name to set
 * @return This field definition for method chaining
 */
FieldDefinition& FieldDefinition::setName(const std::string& name) {
    this->name = name;
    return *this;
}

/**
 * @brief Gets the name of this field definition
 * 
 * @return The name
 */
std::string FieldDefinition::getName() const {
    return name;
}

/**
 * @brief Gets the associated object type
 * 
 * @return The object type
 */
Type* FieldDefinition::getObjectType() const {
    return objectType;
}

/**
 * @brief Gets the field ID
 * 
 * @return The field ID
 */
int FieldDefinition::getId() const {
    return fieldId;
}

/**
 * @brief Sets the associated object type
 * 
 * @param objectType The object type to set
 * @return This field definition for method chaining
 */
FieldDefinition& FieldDefinition::setObjectType(Type* objectType) {
    this->objectType = objectType;
    return *this;
}

/**
 * @brief Gets the tolerance threshold
 * 
 * @return The tolerance threshold
 */
std::optional<double> FieldDefinition::getTolerance() const {
    return tolerance;
}

/**
 * @brief Sets the tolerance threshold
 * 
 * @param tolerance The tolerance to set
 * @return This field definition for method chaining
 */
FieldDefinition& FieldDefinition::setTolerance(std::optional<double> tolerance) {
    this->tolerance = tolerance;
    return *this;
}

/**
 * @brief Gets the processing phase
 * 
 * @return The processing phase
 */
ProcessingPhase* FieldDefinition::getPhase() const {
    return phase;
}

/**
 * @brief Sets the processing phase
 * 
 * @param phase The phase to set
 * @return This field definition for method chaining
 */
FieldDefinition& FieldDefinition::setPhase(ProcessingPhase* phase) {
    this->phase = phase;
    return *this;
}

/**
 * @brief Gets the next round flag
 * 
 * @return true if field should be processed in next round
 */
bool FieldDefinition::getIsNextRound() const {
    return isNextRound;
}

/**
 * @brief Sets the next round flag
 * 
 * @param nextRound The value to set
 * @return This field definition for method chaining
 */
FieldDefinition& FieldDefinition::setNextRound(bool nextRound) {
    isNextRound = nextRound;
    return *this;
}

/**
 * @brief Sets the field to be queued in a specific phase
 * 
 * @param phase The phase to queue in
 * @return This field definition for method chaining
 */
FieldDefinition& FieldDefinition::setQueued(ProcessingPhase* phase) {
    this->phase = phase;
    return *this;
}

/**
 * @brief Converts the field definition to a string representation
 * 
 * @return String representation in the format "id:name"
 */
std::string FieldDefinition::toString() const {
    std::stringstream ss;
    ss << getId() << ":" << name;
    return ss.str();
}

/**
 * @brief Less-than comparison operator for sorting
 * 
 * Compares field definitions based on their IDs.
 * 
 * @param fd The field definition to compare against
 * @return true if this field definition is less than the other
 */
bool FieldDefinition::operator<(const FieldDefinition& fd) const {
    return fieldId < fd.fieldId;
}

