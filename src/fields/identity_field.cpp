#include "fields/identity_field.h"
#include "fields/field_link_definition.h"

// Constructor
IdentityField::IdentityField(Type* ref, const std::string& name)
    : AbstractFunctionDefinition(ref, name, 1, 0.0) {}

// Overridden computeUpdate method
// Identity function: simply returns the first input value unchanged
double IdentityField::computeUpdate(Object* obj, FieldLinkDefinition* fl, double u) {
    // Identity function: return the input value as-is
    // For dot-product secondary links, this will be the input activation value
    return getInputs()[0]->getInputValue(obj);
}