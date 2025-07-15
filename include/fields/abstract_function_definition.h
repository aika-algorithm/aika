/**
 * @file abstract_function_definition.h
 * @brief Defines the AbstractFunctionDefinition class, which represents mathematical functions in the field graph.
 * 
 * The AbstractFunctionDefinition class provides a base for mathematical functions
 * that operate on a fixed number of inputs in the field graph. It handles the
 * computation and propagation of updates based on input changes.
 */

#ifndef ABSTRACT_FUNCTION_DEFINITION_H
#define ABSTRACT_FUNCTION_DEFINITION_H

#include "fields/field_definition.h"
#include "fields/type.h"
#include "fields/object.h"
#include "fields/field.h"

/**
 * @class AbstractFunctionDefinition
 * @brief Base class for mathematical functions with fixed arguments.
 * 
 * The AbstractFunctionDefinition class extends FieldDefinition to provide:
 * - Fixed number of input arguments
 * - Abstract computation interface
 * - Update propagation logic
 * 
 * This class serves as the foundation for specific mathematical operations
 * like addition, multiplication, or exponential functions in the field graph.
 */
class AbstractFunctionDefinition : public FieldDefinition {

public:
    // Constructors
    AbstractFunctionDefinition(Type* objectType, const std::string& name, int numArgs, double tolerance);

    // Destructor
    virtual ~AbstractFunctionDefinition() = default;


    /**
     * @brief Computes the update value based on input changes
     * 
     * This pure virtual method must be implemented by concrete function classes
     * to define their specific mathematical operation.
     * 
     * @param inputs Array of input values
     * @param updates Array of input updates
     * @param numInputs Number of inputs (must match constructor value)
     * @return The computed update value
     */
    virtual double computeUpdate(Object* obj, FieldLinkDefinition* fl, double u) = 0;

    /**
     * @brief Transmits an update to connected fields
     * 
     * Propagates the computed update through the field graph,
     * respecting tolerance thresholds and update rules.
     * 
     * @param field The field being updated
     * @param update The update value to transmit
     */
    void transmit(Field* targetField, FieldLinkDefinition* fl, double u) override;
};

#endif // ABSTRACT_FUNCTION_DEFINITION_H
