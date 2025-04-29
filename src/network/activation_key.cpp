#include "network/activation_key.h"

ActivationKey::ActivationKey(long neuronId, int actId) : neuronId(neuronId), actId(actId) {}

long ActivationKey::getNeuronId() const {
    return neuronId;
}

int ActivationKey::getActId() const {
    return actId;
} 