
#ifndef QUEUE_KEY_H
#define QUEUE_KEY_H

#include <memory>
#include <functional>
#include <string>

class QueueKey;

class ProcessingPhase {
public:
    virtual int rank() const = 0; // Pure virtual function to get the rank of the phase
    virtual bool isDelayed() const = 0; // Pure virtual function to check if the phase is delayed

    virtual ~ProcessingPhase() = default; // Virtual destructor for proper cleanup
};

class QueueKey : public std::enable_shared_from_this<QueueKey> {
public:
    static const int MAX_ROUND = std::numeric_limits<int>::max();
    static const std::function<bool(const std::shared_ptr<QueueKey>, const std::shared_ptr<QueueKey>)> COMPARATOR;

    QueueKey(int round, std::shared_ptr<ProcessingPhase> phase, std::shared_ptr<long> currentTimestamp);

    int getRound() const;
    std::string getRoundStr() const;
    std::shared_ptr<ProcessingPhase> getPhase() const;
    std::string getPhaseStr() const;
    std::shared_ptr<long> getCurrentTimestamp() const;

    bool operator<(const std::shared_ptr<QueueKey>& other) const;

private:
    int round;
    std::shared_ptr<ProcessingPhase> phase;
    std::shared_ptr<long> currentTimestamp;
};

struct QueueKeyComparator {
    bool operator()(const QueueKey& lhs, const QueueKey& rhs) const {
        // Custom comparison logic, could be similar to the `operator<` in QueueKey
        if (lhs.getRound() != rhs.getRound()) return lhs.getRound() < rhs.getRound();
        if (lhs.getPhase() != rhs.getPhase()) return lhs.getPhase() < rhs.getPhase();
        return lhs.getCurrentTimestamp() < rhs.getCurrentTimestamp();
    }
};

#endif //QUEUE_KEY_H
