
#ifndef INPUT_FIELD_H
#define INPUT_FIELD_H


#include "fields/abstract_function_definition.h"
#include "fields/type.h"
#include "fields/object.h"

class InputField : public AbstractFunctionDefinition {

public:

    // Constructor
    InputField(Type* ref, const std::string& name);

    // Overridden method from AbstractFunctionDefinition
    double computeUpdate(Object* obj, FieldLinkDefinition* fl, double u) override;
};

#endif //INPUT_FIELD_H
