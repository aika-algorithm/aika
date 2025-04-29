#ifndef NETWORK_NEURON_SERIALIZATION_EXCEPTION_H
#define NETWORK_NEURON_SERIALIZATION_EXCEPTION_H

#include <stdexcept>
#include <string>

class NeuronSerializationException : public std::runtime_error {
public:
    NeuronSerializationException(long neuronId, const std::exception& cause);
    const std::exception& getCause() const;

private:
    const std::exception& cause;
};

#endif // NETWORK_NEURON_SERIALIZATION_EXCEPTION_H 