#ifndef ADDITION_H
#define ADDITION_H

#include "fields/abstract_function_definition.h"
#include "fields/type.h"
#include "fields/object.h"

class Addition : public AbstractFunctionDefinition {
public:

    // Constructor
    Addition(Type* ref, const std::string& name);

    // Overridden method from AbstractFunctionDefinition
    double computeUpdate(Object* obj, FieldLinkDefinition* fl, double u) override;
};

#endif // ADDITION_H 