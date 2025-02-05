
#ifndef QUEUE_INTERCEPTOR_H
#define QUEUE_INTERCEPTOR_H

#include <memory>
#include "fields/field.h"
#include "fields/queue.h"
#include "fields/queue_key.h"
#include "fields/field_update.h"

class QueueInterceptor {
private:
    std::shared_ptr<ProcessingPhase> phase;
    std::shared_ptr<FieldUpdate> step;
    std::shared_ptr<Field> field;
    std::shared_ptr<Queue> queue;
    bool isNextRound;

public:
    QueueInterceptor(std::shared_ptr<Queue> q,
                     std::shared_ptr<Field> f,
                     std::shared_ptr<ProcessingPhase> phase,
                     bool isNextRound);

    std::shared_ptr<FieldUpdate> getStep() const;
    std::shared_ptr<Field> getField() const;
    bool getIsNextRound() const;
    void receiveUpdate(double u, bool replaceUpdate);
    void process(std::shared_ptr<FieldUpdate> s);
    std::shared_ptr<Queue> getQueue() const;

private:
    std::shared_ptr<FieldUpdate> getOrCreateStep();
};

#endif //QUEUE_INTERCEPTOR_H
