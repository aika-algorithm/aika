
#ifndef FIELD_UPDATE_H
#define FIELD_UPDATE_H

#include <memory>
#include <cmath>
#include <string>
#include <iostream>

#include "fields/step.h"
#include "fields/queue_interceptor.h"
#include "fields/obj.h"

template <typename E>
class FieldUpdate : public Step<E>, Obj, QueueProvider {
private:
    std::shared_ptr<QueueInterceptor> interceptor;
    std::shared_ptr<ProcessingPhase> phase;
    int sortValue = INT_MAX;
    double delta = 0.0;

    void updateSortValue(double delta);

public:
    FieldUpdate(std::shared_ptr<ProcessingPhase> p, std::shared_ptr<QueueInterceptor> qf);

    bool incrementRound() override;
    void updateDelta(double delta, bool replaceUpdate);
    void reset();

    int getSortValue() const;
    double getDelta() const;
    std::shared_ptr<Queue> getQueue() const override;
    void createQueueKey(long timestamp, int round) override;
    void process() override;

    std::shared_ptr<ProcessingPhase> getPhase() const override;
    std::shared_ptr<E> getElement() const override;

    std::shared_ptr<QueueInterceptor> getInterceptor() const;
    std::string toShortString() const;
    std::string toString() const;
};

#endif //FIELD_UPDATE_H
