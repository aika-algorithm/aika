#include "network/model_provider.h"

Model* ModelProvider::getModel() {
    return model;
}

Config* ModelProvider::getConfig() {
    return model->getConfig();
} 