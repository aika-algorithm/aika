#include "fields/field_activation_function.h"

FieldActivationFunction::FieldActivationFunction(Type* ref, const std::string& name, ActivationFunction* actFunction, double tolerance)
    : AbstractFunctionDefinition(ref, name, 1, tolerance), actFunction(actFunction) {}


double FieldActivationFunction::computeUpdate(Obj* obj, FieldLinkDefinition* fl, double u) {
    double value = obj->getOrCreateFieldInput(this)->getValue();
    return actFunction->f(fl->getUpdatedInputValue(obj)) - value;
} 