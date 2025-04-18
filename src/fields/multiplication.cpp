#include "fields/multiplication.h"

// Constructor
Multiplication::Multiplication(Type* ref, const std::string& name)
    : AbstractFunctionDefinition(ref, name, 2, 0.0) {}

void Multiplication::initializeField(Field* field) {
    Obj* toObj = field->getObject();
    FieldDefinition* fieldDef = field->getFieldDefinition();
    std::vector<FieldLinkDefinition*> inputs = fieldDef->getInputs();
    double valueArg0 = inputs[0]->getInputValue(toObj);
    double valueArg1 = inputs[1]->getInputValue(toObj);

    field->setValue(valueArg0 * valueArg1);
}

// Overridden computeUpdate method
double Multiplication::computeUpdate(Obj* obj, FieldLinkDefinition* fl, double u) {
    return u * getInputs()[fl->getArgument() == 0 ? 1 : 0]->getInputValue(obj);
} 