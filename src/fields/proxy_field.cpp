#include "fields/proxy_field.h"
#include "fields/field.h"

// Constructor
ProxyField::ProxyField(Type* objectType, const std::string& name, FieldDefinition* targetField)
    : FieldDefinition(objectType, name, 0, 0.0), targetField(targetField) {
    if (targetField == nullptr) {
        throw std::invalid_argument("ProxyField target cannot be null");
    }
}

// Get the target field
FieldDefinition* ProxyField::getTargetField() const {
    return targetField;
}

// Set the target field
void ProxyField::setTargetField(FieldDefinition* target) {
    if (target == nullptr) {
        throw std::invalid_argument("ProxyField target cannot be null");
    }
    targetField = target;
}

// Transmit update by forwarding to the target field
void ProxyField::transmit(Field* field, FieldLinkDefinition* fieldLink, double update) {
    // Forward the transmit call to the target field definition
    targetField->transmit(field, fieldLink, update);
}