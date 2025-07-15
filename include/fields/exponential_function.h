#ifndef EXPONENTIAL_FUNCTION_H
#define EXPONENTIAL_FUNCTION_H

#include "fields/abstract_function_definition.h"
#include "fields/type.h"
#include "fields/object.h"

class ExponentialFunction : public AbstractFunctionDefinition {
public:
    // Constructor
    ExponentialFunction(Type* ref, const std::string& name);

    void initializeField(Field* field) override;
    
    // Overridden method from AbstractFunctionDefinition
    double computeUpdate(Object* obj, FieldLinkDefinition* fl, double u) override;
};

#endif // EXPONENTIAL_FUNCTION_H 