#ifndef NETWORK_FIRED_H
#define NETWORK_FIRED_H

#include "fields/step.h"
#include "fields/queue.h"
#include "network/activation.h"
#include "network/phase.h"
#include "network/timestamp.h"
#include <string>

class Fired : public Step {
public:
    Fired(Activation* act);
    virtual ~Fired() = default;

    void createQueueKey(long timestamp, int round) override;
    void process() override;
    void updateNet(double net);
    const Phase& getPhase() const override;
    Activation* getElement();
    Queue* getQueue() const override;
    std::string toString() const override;
    bool isQueued() const;

private:
    Activation* act;
    double net;
    int sortValue;
};

#endif // NETWORK_FIRED_H 