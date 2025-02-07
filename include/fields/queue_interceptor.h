
#ifndef QUEUE_INTERCEPTOR_H
#define QUEUE_INTERCEPTOR_H

#include "fields/field.h"
#include "fields/field_update.h"

class QueueInterceptor {
private:
    ProcessingPhase* phase;
    FieldUpdate* step;
    Field* field;
    Queue* queue;
    bool isNextRound;

public:
    QueueInterceptor(Queue* q,
                     Field* f,
                     ProcessingPhase* phase,
                     bool isNextRound);

    FieldUpdate* getStep() const;
    Field* getField() const;
    bool getIsNextRound() const;
    void receiveUpdate(double u, bool replaceUpdate);
    void process(FieldUpdate* s);
    Queue* getQueue() const;

private:
    FieldUpdate* getOrCreateStep();
};

#endif //QUEUE_INTERCEPTOR_H
