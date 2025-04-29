#ifndef NETWORK_ACTIVATION_KEY_H
#define NETWORK_ACTIVATION_KEY_H

class ActivationKey {
public:
    ActivationKey(long neuronId, int actId);
    long getNeuronId() const;
    int getActId() const;

private:
    long neuronId;
    int actId;
};

#endif // NETWORK_ACTIVATION_KEY_H 