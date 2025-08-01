#ifndef MULTIPLICATION_H
#define MULTIPLICATION_H

#include "fields/abstract_function_definition.h"
#include "fields/type.h"
#include "fields/object.h"

class Multiplication : public AbstractFunctionDefinition {
public:

    // Constructor
    Multiplication(Type* ref, const std::string& name);

    void initializeField(Field* field) override;

    // Overridden method from AbstractFunctionDefinition
    double computeUpdate(Object* obj, FieldLinkDefinition* fl, double u) override;
};

#endif // MULTIPLICATION_H 