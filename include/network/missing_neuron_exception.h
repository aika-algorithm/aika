#ifndef NETWORK_MISSING_NEURON_EXCEPTION_H
#define NETWORK_MISSING_NEURON_EXCEPTION_H

#include <stdexcept>
#include <string>

class MissingNeuronException : public std::runtime_error {
public:
    MissingNeuronException(long id, const std::string& modelLabel);
};

#endif // NETWORK_MISSING_NEURON_EXCEPTION_H 