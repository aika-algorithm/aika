#ifndef STEP_H
#define STEP_H

#include <memory>

#include "queue_provider.h"
#include "fields/queue_key.h"

class Queue;


class Step : public QueueProvider {
    protected:
    bool isQueued;
    QueueKey* queueKey;

    public:
    Step() : isQueued(false), queueKey(nullptr) {}

    void setIsQueued(bool queued);
    bool getIsQueued() const;
    QueueKey* getQueueKey() const;

    virtual Queue* getQueue() const = 0;
    virtual bool incrementRound();
    virtual void createQueueKey(long timestamp, int round) = 0;
    virtual void process() = 0;
    virtual ProcessingPhase& getPhase() const = 0;
    static bool add(Step* s);
};

#endif // STEP_H

