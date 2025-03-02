
#include "fields/input_field.h"

// Constructor
InputField::InputField(Type* ref, const std::string& name)
    : AbstractFunctionDefinition(ref, name, &zero, 0.0) {}

// Overridden computeUpdate method
double InputField::computeUpdate(Obj* obj, FieldLinkDefinition* fl, double u) {
    return 0;
}