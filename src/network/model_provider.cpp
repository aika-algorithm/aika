#include "network/model_provider.h"

Model* ModelProvider::getModel() const {
    return model;
}

Config* ModelProvider::getConfig() const {
    return model->getConfig();
} 