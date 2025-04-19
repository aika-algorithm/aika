#include "fields/summation.h"

// Constructor
Summation::Summation(Type* ref, const std::string& name)
    : AbstractFunctionDefinition(ref, name, 2, 0.0) {}

// Overridden computeUpdate method
double Summation::computeUpdate(Obj* obj, FieldLinkDefinition* fl, double u) {
    return u;
} 