#include "fields/queue.h"
#include "fields/step.h"
#include "fields/queue_key.h"
#include <iostream>
#include <cassert>

long Queue::getTimeout() {
    return std::numeric_limits<long>::max();
}

long Queue::getTimestampOnProcess() {
    return timestampOnProcess;
}

long Queue::getCurrentTimestamp() {
    return timestampCounter;
}

long Queue::getNextTimestamp() {
    return timestampCounter++;
}

void Queue::addStep(Step* s) {
    s->createQueueKey(getNextTimestamp(), getRound(s));
    queue[s->getQueueKey()] = s;
    s->setQueued(true);
}

int Queue::getRound(Step* s) {
    int round;
    if (s->getPhase()->isDelayed())
        round = QueueKey::MAX_ROUND;
    else
        round = getCurrentRound();

    if (s->incrementRound())
        round++;

    return round;
}

int Queue::getCurrentRound() {
    if (!currentStep)
        return 0;

    int r = currentStep->getQueueKey()->getRound();
    return (r == QueueKey::MAX_ROUND) ? 0 : r;
}

void Queue::removeStep(Step* s) {
    auto removedStep = queue.erase(s->getQueueKey());
    if (removedStep == 0)
        throw std::runtime_error("Step not found");
    s->setQueued(false);
}

std::vector<Step*> Queue::getQueueEntries() {
    std::vector<Step*> entries;
    for (const auto& entry : queue) {
        entries.push_back(entry.second);
    }
    return entries;
}

void Queue::process() {
    process(nullptr);
}

void Queue::process(std::function<bool(Step*)> filter) {
    long startTime = std::chrono::system_clock::now().time_since_epoch().count();

    while (!queue.empty()) {
        checkTimeout(startTime);

        currentStep = queue.begin()->second;
        queue.erase(queue.begin());
        currentStep->setQueued(false);

        timestampOnProcess = getCurrentTimestamp();
        if (!filter || filter(currentStep)) {
            currentStep->process();
        }

        currentStep = nullptr;
    }
}

void Queue::checkTimeout(long startTime) {
    long timeout = getTimeout();
    if (timeout == std::numeric_limits<long>::max())
        return;

    long currentTime = std::chrono::system_clock::now().time_since_epoch().count();
    if (startTime + timeout < currentTime)
        throw std::runtime_error("Timeout Exception");
}

