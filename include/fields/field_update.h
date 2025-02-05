
#ifndef FIELD_UPDATE_H
#define FIELD_UPDATE_H

#include <memory>
#include <cmath>
#include <string>
#include <iostream>

#include "fields/step.h"
#include "fields/queue_interceptor.h"
#include "fields/obj.h"


class FieldUpdate : public Step, Obj {
private:
    std::shared_ptr<QueueInterceptor> interceptor;
    std::shared_ptr<ProcessingPhase> phase;
    int sortValue = INT_MAX;
    double delta = 0.0;

    void updateSortValue(double delta);

public:
    FieldUpdate(std::shared_ptr<ProcessingPhase> p, std::shared_ptr<QueueInterceptor> qf);

    bool incrementRound();
    void updateDelta(double delta, bool replaceUpdate);
    void reset();

    int getSortValue() const;
    double getDelta() const;
    std::shared_ptr<Queue> getQueue() const;
    void createQueueKey(long timestamp, int round);
    void process();

    std::shared_ptr<ProcessingPhase> getPhase() const;

    std::shared_ptr<QueueInterceptor> getInterceptor() const;
    std::string toShortString() const;
    std::string toString() const;
};

#endif //FIELD_UPDATE_H
