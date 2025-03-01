#include "fields/abstract_function_definition.h"

// Constructor with 3 arguments
AbstractFunctionDefinition::AbstractFunctionDefinition(Type* objectType, const std::string& name, int numArgs)
    : FieldDefinition(objectType, name, numArgs) {}

// Constructor with 4 arguments
AbstractFunctionDefinition::AbstractFunctionDefinition(Type* objectType, const std::string& name, int numArgs, double tolerance)
    : FieldDefinition(objectType, name, numArgs, tolerance) {}

// Transmit method
void AbstractFunctionDefinition::transmit(Field* targetField, FieldLinkDefinition* fl, double u) {
    double update = computeUpdate(targetField->getObject(), fl, u);
    receiveUpdate(targetField, update);
}
