/**
 * @file abstract_function_definition.cpp
 * @brief Implements the AbstractFunctionDefinition class functionality.
 * 
 * This file contains the implementation of the AbstractFunctionDefinition class,
 * which provides the foundation for mathematical functions in the field graph.
 * It handles initialization and update propagation for functions with fixed arguments.
 */

#include "fields/abstract_function_definition.h"


/**
 * @brief Constructs a new AbstractFunctionDefinition
 * 
 * Initializes the function definition with the given name, number of inputs,
 * and tolerance threshold. Sets up the base FieldDefinition with appropriate
 * parameters.
 * 
 * @param name The name of the function
 * @param numInputs Number of input arguments
 * @param tolerance Optional tolerance threshold for updates
 */
AbstractFunctionDefinition::AbstractFunctionDefinition(Type* objectType, const std::string& name, int numArgs, double tolerance) 
    : FieldDefinition(objectType, name, numArgs, tolerance) {}


/**
 * @brief Transmits an update to connected fields
 * 
 * When an update is received:
 * 1. Checks if the update exceeds the tolerance threshold
 * 2. If significant, computes the update using the concrete function's logic
 * 3. Propagates the computed update to connected fields
 * 
 * @param field The field being updated
 * @param update The update value to transmit
 */
void AbstractFunctionDefinition::transmit(Field* targetField, FieldLinkDefinition* fl, double u) {
    double update = computeUpdate(targetField->getObject(), fl, u);
    receiveUpdate(targetField, update);
}
