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

struct ActivationKeyComparator {
    bool operator()(const ActivationKey& a, const ActivationKey& b) const {
        if(a.getNeuronId() != b.getNeuronId())
            return a.getNeuronId() < b.getNeuronId();
        else
            return a.getActId() < b.getActId();
    }
};

#endif // NETWORK_ACTIVATION_KEY_H 