#ifndef NETWORK_MODEL_PROVIDER_H
#define NETWORK_MODEL_PROVIDER_H

#include "network/model.h"
#include "network/config.h"

class ModelProvider {
public:
    virtual Model* getModel();
    virtual Config* getConfig();

protected:
    Model* model;
};

#endif // NETWORK_MODEL_PROVIDER_H 