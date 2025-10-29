#ifndef IDENTITY_FIELD_H
#define IDENTITY_FIELD_H

#include "fields/abstract_function_definition.h"
#include "fields/type.h"
#include "fields/object.h"

class IdentityField : public AbstractFunctionDefinition {

public:
    // Constructor
    IdentityField(Type* ref, const std::string& name);

    // Overridden method from AbstractFunctionDefinition
    double computeUpdate(Object* obj, FieldLinkDefinition* fl, double u) override;
};

#endif //IDENTITY_FIELD_H