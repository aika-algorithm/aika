#include "fields/division.h"

// Constructor
Division::Division(Type* ref, const std::string& name)
    : AbstractFunctionDefinition(ref, name, 2, 0.0) {}

void Division::initializeField(Field* field) {
    Obj* toObj = field->getObject();
    FieldDefinition* fieldDef = field->getFieldDefinition();
    std::vector<FieldLinkDefinition*> inputs = fieldDef->getInputs();
    double dividend = inputs[0]->getInputValue(toObj);
    double divisor = inputs[1]->getInputValue(toObj);

    if (divisor == 0.0) {
        // Handle division by zero if necessary
        return;
    }

    field->setValue(dividend / divisor);
}

// Overridden computeUpdate method
// This method computes the update for the division operation
// based on which argument is being updated.
double Division::computeUpdate(Obj* obj, FieldLinkDefinition* fl, double u) {
    if (fl->getArgument() == 0) {
        double divisor = getInputs()[1]->getInputValue(obj);
        if (divisor == 0.0) {
            // Handle division by zero if necessary
            return 0.0;
        }
        return u / divisor;
    } else {
        double dividend = getInputs()[0]->getInputValue(obj);
        double divisor = getInputs()[1]->getUpdatedInputValue(obj);
        if (divisor == 0.0) {
            // Handle division by zero if necessary
            return 0.0;
        }
        double oldValue = obj->getFieldValue(this);
        return (dividend / divisor) - oldValue;
    }
} 