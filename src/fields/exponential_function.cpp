#include "fields/exponential_function.h"
#include <cmath>

// Constructor
ExponentialFunction::ExponentialFunction(Type* ref, const std::string& name)
    : AbstractFunctionDefinition(ref, name, 1, 0.0) {}

void ExponentialFunction::initializeField(Field* field) {
    Obj* toObj = field->getObject();
    double valueArg0 = inputs[0]->getInputValue(toObj);

    field->setValue(std::exp(valueArg0));
}

// Overridden computeUpdate method
double ExponentialFunction::computeUpdate(Obj* obj, FieldLinkDefinition* fl, double u) {
    double valueArg0 = inputs[0]->getUpdatedInputValue(obj);
    return std::exp(valueArg0) - obj->getFieldValue(this);
} 