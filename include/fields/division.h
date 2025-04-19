#ifndef DIVISION_H
#define DIVISION_H

#include "fields/abstract_function_definition.h"
#include "fields/type.h"
#include "fields/obj.h"

class Division : public AbstractFunctionDefinition {
public:

    // Constructor
    Division(Type* ref, const std::string& name);

    void initializeField(Field* field) override;

    // Overridden method from AbstractFunctionDefinition
    double computeUpdate(Obj* obj, FieldLinkDefinition* fl, double u) override;
};

#endif // DIVISION_H 