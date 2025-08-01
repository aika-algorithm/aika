#ifndef SUBTRACTION_H
#define SUBTRACTION_H

#include "fields/abstract_function_definition.h"
#include "fields/type.h"
#include "fields/object.h"


class Subtraction : public AbstractFunctionDefinition {
public:

    // Constructor
    Subtraction(Type* ref, const std::string& name);

    // Overridden method from AbstractFunctionDefinition
    double computeUpdate(Object* obj, FieldLinkDefinition* fl, double u) override;
};

#endif // SUBTRACTION_H
