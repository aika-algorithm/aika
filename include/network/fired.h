#ifndef NETWORK_FIRED_H
#define NETWORK_FIRED_H

#include "fields/step.h"
#include "network/activation.h"
#include "network/phase.h"
#include "network/queue.h"
#include <string>

class Fired : public Step<Activation> {
public:
    Fired(Activation* act);

    void createQueueKey(Timestamp timestamp, int round) override;
    void process() override;
    void updateNet(double net);
    Phase getPhase() override;
    Activation* getElement() override;
    Queue* getQueue() override;
    std::string toString() override;

private:
    Activation* act;
    double net;
    int sortValue;
};

#endif // NETWORK_FIRED_H 