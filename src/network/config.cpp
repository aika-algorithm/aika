#include "network/config.h"

Config::Config() : alpha(0.0), learnRate(0.0), trainingEnabled(false), countingEnabled(false), neuronProviderRetention(50), timeout(0) {}

double Config::getLearnRate() const {
    return learnRate;
}

Config& Config::setLearnRate(double learnRate) {
    this->learnRate = learnRate;
    return *this;
}

double Config::getAlpha() const {
    return alpha;
}

Config& Config::setAlpha(double alpha) {
    this->alpha = alpha;
    return *this;
}

bool Config::isTrainingEnabled() const {
    return trainingEnabled;
}

Config& Config::setTrainingEnabled(bool trainingEnabled) {
    this->trainingEnabled = trainingEnabled;
    return *this;
}

bool Config::isCountingEnabled() const {
    return countingEnabled;
}

Config& Config::setCountingEnabled(bool countingEnabled) {
    this->countingEnabled = countingEnabled;
    return *this;
}

long Config::getNeuronProviderRetention() const {
    return neuronProviderRetention;
}

Config& Config::setNeuronProviderRetention(long neuronProviderRetention) {
    this->neuronProviderRetention = neuronProviderRetention;
    return *this;
}

long Config::getTimeout() const {
    return timeout;
}

Config& Config::setTimeout(long timeout) {
    this->timeout = timeout;
    return *this;
}

std::string Config::toString() const {
    return "Alpha: " + std::to_string(alpha) + "\n" +
           "LearnRate: " + std::to_string(learnRate) + "\n\n";
} 