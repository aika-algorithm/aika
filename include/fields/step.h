#ifndef STEP_H
#define STEP_H

#include <memory>

#include "fields/queue.h"
#include "fields/queue_key.h"

template <typename E>
class Step : public QueueProvider {
    protected:
    bool isQueued;
    std::shared_ptr<QueueKey> queueKey;

    public:
    Step() : isQueued(false), queueKey(nullptr) {}

    void setIsQueued(bool queued);
    bool getIsQueued() const;
    std::shared_ptr<QueueKey> getQueueKey() const;

    virtual std::shared_ptr<Queue> getQueue() const = 0;
    virtual bool incrementRound();
    virtual void createQueueKey(std::shared_ptr<long> timestamp, int round) = 0;
    virtual void process() = 0;
    virtual std::shared_ptr<ProcessingPhase> getPhase() const = 0;
    static bool add(std::shared_ptr<Step<E>> s);
    virtual std::shared_ptr<E> getElement() const = 0;
};

#endif // STEP_H

