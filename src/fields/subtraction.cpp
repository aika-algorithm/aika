#include "fields/subtraction.h"


// Constructor
Subtraction::Subtraction(Type* ref, const std::string& name)
    : AbstractFunctionDefinition(ref, name, 0, 0.0) {}

// Overridden computeUpdate method
double Subtraction::computeUpdate(Obj* obj, FieldLinkDefinition* fl, double u) {
    return (fl->getArgument() == 0) ? u : -u;
}
