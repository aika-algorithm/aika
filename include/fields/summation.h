#ifndef SUM_H
#define SUM_H

#include "fields/abstract_function_definition.h"
#include "fields/type.h"
#include "fields/obj.h"

class Summation : public AbstractFunctionDefinition {
public:

    // Constructor
    Summation(Type* ref, const std::string& name);

    // Overridden method from AbstractFunctionDefinition
    double computeUpdate(Obj* obj, FieldLinkDefinition* fl, double u) override;
};

#endif // SUM_H 