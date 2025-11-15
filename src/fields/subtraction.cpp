#include "fields/subtraction.h"
#include "fields/field_link_definition.h"


// Constructor
Subtraction::Subtraction(Type* ref, const std::string& name)
    : AbstractFunctionDefinition(ref, name, 2, 0.0) {}

// Overridden computeUpdate method
double Subtraction::computeUpdate(Object* obj, FieldLinkDefinition* fl, double u) {
    return (fl->getArgument() == 0) ? u : -u;
}
