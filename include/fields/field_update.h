
#ifndef FIELD_UPDATE_H
#define FIELD_UPDATE_H

#include <string>

#include "fields/obj.h"
#include "fields/step.h"
#include "fields/queue.h"


class FieldUpdate : public Step {
private:
    QueueInterceptor* interceptor;
    ProcessingPhase& phase;
    int sortValue = INT_MAX;
    double delta = 0.0;

    void updateSortValue(double delta);

public:
    FieldUpdate(ProcessingPhase& p, QueueInterceptor* qf);

    bool incrementRound();
    void updateDelta(double delta, bool replaceUpdate);
    void reset();

    int getSortValue() const;
    double getDelta() const;
    Queue* getQueue() const;
    void createQueueKey(long timestamp, int round);
    void process();

    ProcessingPhase& getPhase() const;

    QueueInterceptor* getInterceptor() const;
    std::string toShortString() const;
    std::string toString() const;
};

#endif //FIELD_UPDATE_H
