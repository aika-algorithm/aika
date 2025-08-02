#ifndef FIELD_ACTIVATION_FUNCTION_H
#define FIELD_ACTIVATION_FUNCTION_H

#include "fields/abstract_function_definition.h"
#include "fields/type.h"
#include "fields/object.h"
#include "fields/field_link_definition.h"
#include "fields/activation_function.h"

class FieldActivationFunction : public AbstractFunctionDefinition {
public:
    FieldActivationFunction(Type* ref, const std::string& name, ActivationFunction* actFunction, double tolerance);

    double computeUpdate(Object* obj, FieldLinkDefinition* fl, double u) override;

private:
    ActivationFunction* actFunction;
    double tolerance;
};

#endif // FIELD_ACTIVATION_FUNCTION_H 