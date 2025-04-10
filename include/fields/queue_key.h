
#ifndef QUEUE_KEY_H
#define QUEUE_KEY_H

#include <functional>
#include <string>


class ProcessingPhase {
public:
    virtual int rank() const = 0; // Pure virtual function to get the rank of the phase
    virtual bool isDelayed() const = 0; // Pure virtual function to check if the phase is delayed

    virtual ~ProcessingPhase() = default; // Virtual destructor for proper cleanup
};

class QueueKey {
public:
    static const int MAX_ROUND = std::numeric_limits<int>::max();

    QueueKey(int round, ProcessingPhase& phase, long currentTimestamp);

    int getRound() const;
    std::string getRoundStr() const;
    ProcessingPhase& getPhase() const;
    std::string getPhaseStr() const;
    long getCurrentTimestamp() const;

    bool operator<(const QueueKey& other) const;

private:
    int round;
    ProcessingPhase& phase;
    long currentTimestamp;
};

struct QueueKeyComparator {
    bool operator()(const QueueKey* lhs, const QueueKey* rhs) const {
        // Custom comparison logic, could be similar to the `operator<` in QueueKey
        if (lhs->getRound() != rhs->getRound()) return lhs->getRound() < rhs->getRound();
        if (lhs->getPhase().rank() != rhs->getPhase().rank()) return lhs->getPhase().rank() < rhs->getPhase().rank();
        return lhs->getCurrentTimestamp() < rhs->getCurrentTimestamp();
    }
};


class FieldQueueKey : public QueueKey {
public:
    // Constructor
    FieldQueueKey(int round, ProcessingPhase& phase, int sortValue, long currentTimestamp);

    // Getters
    int getSortValue() const;

    // Overridden Methods
    bool operator<(const QueueKey& other) const;
    std::string toString() const;

private:
    // Private members
    int sortValue;

    // Helper method for string conversion
    std::string getSortValueAsString() const;
};

#endif //QUEUE_KEY_H
