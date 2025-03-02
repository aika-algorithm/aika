#ifndef ABSTRACT_FUNCTION_DEFINITION_H
#define ABSTRACT_FUNCTION_DEFINITION_H

#include "fields/field_definition.h"
#include "fields/type.h"
#include "fields/obj.h"
#include "fields/field.h"

class AbstractFunctionDefinition : public FieldDefinition {
public:
    // Constructors
    AbstractFunctionDefinition(Type* objectType, const std::string& name, int* numArgs, double tolerance);

    // Destructor
    virtual ~AbstractFunctionDefinition() = default;

    // Abstract method
    virtual double computeUpdate(Obj* obj, FieldLinkDefinition* fl, double u) = 0;

    // Overridden method
    void transmit(Field* targetField, FieldLinkDefinition* fl, double u) override;
};

#endif // ABSTRACT_FUNCTION_DEFINITION_H
