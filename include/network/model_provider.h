#ifndef NETWORK_MODEL_PROVIDER_H
#define NETWORK_MODEL_PROVIDER_H

#include "network/model.h"
#include "network/config.h"

class ModelProvider {
public:
    virtual ~ModelProvider() = default;
    virtual Model* getModel() const = 0;
    virtual Config* getConfig() const = 0;

protected:
    Model* model;
};

#endif // NETWORK_MODEL_PROVIDER_H 