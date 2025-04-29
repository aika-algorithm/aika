#include "network/neuron_serialization_exception.h"

NeuronSerializationException::NeuronSerializationException(long neuronId, const std::exception& cause)
    : std::runtime_error("Failed to serialize or deserialize the neuron [" + std::to_string(neuronId) + "]"), cause(cause) {}

const std::exception& NeuronSerializationException::getCause() const {
    return cause;
} 