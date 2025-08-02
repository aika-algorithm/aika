#include "fields/field_activation_function.h"

FieldActivationFunction::FieldActivationFunction(Type* ref, const std::string& name, ActivationFunction* actFunction)
    : AbstractFunctionDefinition(ref, name, 1, 0.0), actFunction(actFunction) {}


double FieldActivationFunction::computeUpdate(Object* obj, FieldLinkDefinition* fl, double u) {
    double value = obj->getOrCreateFieldInput(this)->getValue();
    return actFunction->f(fl->getUpdatedInputValue(obj)) - value;
} 