#include "network/activation_definition.h"
#include "network/activation.h"

ActivationDefinition::ActivationDefinition(TypeRegistry* registry, const std::string& name)
    : Type(registry, name) {
    // Constructor implementation
}

ActivationDefinition::~ActivationDefinition() {
    // Destructor implementation
} 