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
    std::shared_ptr<Step> currentStep;
    std::map<std::shared_ptr<QueueKey>, std::shared_ptr<Step>, QueueKeyComparator> queue;
    long timestampCounter = 0;
    long timestampOnProcess = 0;

    int getRound(std::shared_ptr<Step> s);
    void checkTimeout(long startTime);

public:
    long getTimeout();
    long getTimestampOnProcess();
    long getCurrentTimestamp();
    long getNextTimestamp();

    void addStep(std::shared_ptr<Step> s);
    void removeStep(std::shared_ptr<Step> s);
    void process();
    void process(std::function<bool(std::shared_ptr<Step>)> filter);
    std::vector<std::shared_ptr<Step>> getQueueEntries();
    int getCurrentRound();
};

#endif // QUEUE_H
