#include "fields/addition.h"

// Constructor
Addition::Addition(Type* ref, const std::string& name)
    : AbstractFunctionDefinition(ref, name, 2, 0.0) {}

// Overridden computeUpdate method
double Addition::computeUpdate(Object* obj, FieldLinkDefinition* fl, double u) {
    return u;
} 