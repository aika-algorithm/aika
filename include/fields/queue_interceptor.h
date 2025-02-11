
#ifndef QUEUE_INTERCEPTOR_H
#define QUEUE_INTERCEPTOR_H


class FieldUpdate;
class Field;
class Queue;
class ProcessingPhase;

class QueueInterceptor {
private:
    ProcessingPhase& phase;
    FieldUpdate* step;
    Field* field;
    Queue* queue;
    const bool isNextRound;

public:
    QueueInterceptor(Queue* q,
                     Field* f,
                     ProcessingPhase& phase,
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
