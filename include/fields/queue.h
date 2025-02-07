#ifndef QUEUE_H
#define QUEUE_H

#include <memory>
#include <map>
#include <set>
#include <vector>
#include <functional>

#include "fields/step.h"
#include "fields/queue_key.h"


class Queue {
private:
    Step* currentStep;
    std::map<QueueKey*, Step*, QueueKeyComparator> queue;
    long timestampCounter = 0;
    long timestampOnProcess = 0;

    int getRound(Step* s);
    void checkTimeout(long startTime);

public:
    long getTimeout();
    long getTimestampOnProcess();
    long getCurrentTimestamp();
    long getNextTimestamp();

    void addStep(Step* s);
    void removeStep(Step* s);
    void process();
    void process(std::function<bool(Step*)> filter);
    std::vector<Step*> getQueueEntries();
    int getCurrentRound();
};

#endif // QUEUE_H
