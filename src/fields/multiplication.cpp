#include "fields/multiplication.h"

// Constructor
Multiplication::Multiplication(Type* ref, const std::string& name)
    : AbstractFunctionDefinition(ref, name, 2, 0.0) {}

// Overridden computeUpdate method
double Multiplication::computeUpdate(Obj* obj, FieldLinkDefinition* fl, double u) {
    return u * getInputs()[fl->getArgument() == 0 ? 1 : 0]->getInputValue(obj);
} 