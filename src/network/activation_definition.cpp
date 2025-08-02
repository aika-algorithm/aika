#include "network/activation_definition.h"
#include "network/activation.h"

const RelationSelf ActivationDefinition::SELF = RelationSelf(0, "SELF");
const RelationMany ActivationDefinition::INPUT = RelationMany(1, "INPUT");
const RelationMany ActivationDefinition::OUTPUT = RelationMany(2, "OUTPUT");
const RelationOne ActivationDefinition::NEURON = RelationOne(3, "NEURON");

// Static initializer to set up reverse relationships
class ActivationDefinitionInitializer {
public:
    ActivationDefinitionInitializer() {
        // Set up bidirectional relationships
        const_cast<RelationMany&>(ActivationDefinition::INPUT).setReversed(const_cast<RelationMany*>(&ActivationDefinition::OUTPUT));
        const_cast<RelationMany&>(ActivationDefinition::OUTPUT).setReversed(const_cast<RelationMany*>(&ActivationDefinition::INPUT));
        
        // SELF and NEURON are their own reverse
        const_cast<RelationSelf&>(ActivationDefinition::SELF).setReversed(const_cast<RelationSelf*>(&ActivationDefinition::SELF));
        const_cast<RelationOne&>(ActivationDefinition::NEURON).setReversed(const_cast<RelationOne*>(&ActivationDefinition::NEURON));
    }
};

static ActivationDefinitionInitializer activationDefInit;

ActivationDefinition::ActivationDefinition(TypeRegistry* registry, const std::string& name)
    : Type(registry, name) {
    // Constructor implementation
}

ActivationDefinition::~ActivationDefinition() {
    // Destructor implementation
}

std::vector<Relation*> ActivationDefinition::getRelations() const {
    // Return a vector of pointers to avoid the abstract class issue
    return {
        const_cast<RelationSelf*>(&SELF),
        const_cast<RelationMany*>(&INPUT),
        const_cast<RelationMany*>(&OUTPUT),
        const_cast<RelationOne*>(&NEURON)
    };
} 