#include "network/types/activation_type.h"
#include "network/activation.h"

const RelationSelf ActivationType::SELF = RelationSelf(0, "SELF");
const RelationMany ActivationType::INPUT = RelationMany(1, "INPUT");
const RelationMany ActivationType::OUTPUT = RelationMany(2, "OUTPUT");
const RelationOne ActivationType::NEURON = RelationOne(3, "NEURON");

// Static initializer to set up reverse relationships
class ActivationTypeInitializer {
public:
    ActivationTypeInitializer() {
        // Set up bidirectional relationships
        const_cast<RelationMany&>(ActivationType::INPUT).setReversed(const_cast<RelationMany*>(&ActivationType::OUTPUT));
        const_cast<RelationMany&>(ActivationType::OUTPUT).setReversed(const_cast<RelationMany*>(&ActivationType::INPUT));
        
        // SELF and NEURON are their own reverse
        const_cast<RelationSelf&>(ActivationType::SELF).setReversed(const_cast<RelationSelf*>(&ActivationType::SELF));
        const_cast<RelationOne&>(ActivationType::NEURON).setReversed(const_cast<RelationOne*>(&ActivationType::NEURON));
    }
};

static ActivationTypeInitializer activationDefInit;

ActivationType::ActivationType(TypeRegistry* registry, const std::string& name)
    : Type(registry, name) {
    // Constructor implementation
}

ActivationType::~ActivationType() {
    // Destructor implementation
}

std::vector<Relation*> ActivationType::getRelations() const {
    // Return a vector of pointers to avoid the abstract class issue
    return {
        const_cast<RelationSelf*>(&SELF),
        const_cast<RelationMany*>(&INPUT),
        const_cast<RelationMany*>(&OUTPUT),
        const_cast<RelationOne*>(&NEURON)
    };
}

NeuronType* ActivationType::getNeuronType() const {
    return neuronType;
}

void ActivationType::setNeuronType(NeuronType* neuronType) {
    this->neuronType = neuronType;
}