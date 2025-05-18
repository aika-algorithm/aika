#ifndef NETWORK_CONFIG_H
#define NETWORK_CONFIG_H

#include <string>

class Config {
public:
    Config();
    double getLearnRate() const;
    Config& setLearnRate(double learnRate);
    double getAlpha() const;
    Config& setAlpha(double alpha);
    bool isTrainingEnabled() const;
    Config& setTrainingEnabled(bool trainingEnabled);
    bool isCountingEnabled() const;
    Config& setCountingEnabled(bool countingEnabled);
    long getNeuronProviderRetention() const;
    Config& setNeuronProviderRetention(long neuronProviderRetention);
    long getTimeout() const;
    Config& setTimeout(long timeout);
    std::string toString() const;

private:
    double alpha;
    double learnRate;
    bool trainingEnabled;
    bool countingEnabled;
    long neuronProviderRetention;
    long timeout;
};

#endif // NETWORK_CONFIG_H 