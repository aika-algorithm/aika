
#ifndef QUEUE_PROVIDER_H
#define QUEUE_PROVIDER_H


class Queue;


class QueueProvider {
public:
    virtual Queue* getQueue() const; // Pure virtual function

    virtual ~QueueProvider() = default; // Virtual destructor for proper cleanup
};

#endif //QUEUE_PROVIDER_H
