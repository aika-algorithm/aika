#include "network/missing_neuron_exception.h"

MissingNeuronException::MissingNeuronException(long id, const std::string& modelLabel)
    : std::runtime_error("Neuron with id [" + std::to_string(id) + "] is missing in model label " + modelLabel) {} 