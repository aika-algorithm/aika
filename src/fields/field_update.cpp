
#include "fields/field_update.h"
#include "fields/utils.h"
#include <limits>

template <typename E>
FieldUpdate<E>::FieldUpdate(std::shared_ptr<ProcessingPhase> p, std::shared_ptr<QueueInterceptor> qf)
    : phase(p), interceptor(qf) {}

template <typename E>
bool FieldUpdate<E>::incrementRound() {
    return interceptor->isNextRound();
}

template <typename E>
void FieldUpdate<E>::updateSortValue(double delta) {
    int newSortValue = ApproximateComparisonValueUtil::convert(delta);
    if (std::abs(sortValue - newSortValue) == 0) {
        return;
    }

    if (isQueued()) {
        auto q = getQueue();
        q->removeStep(shared_from_this());
        sortValue = newSortValue;
        q->addStep(shared_from_this());
    } else {
        sortValue = newSortValue;
    }
}

template <typename E>
int FieldUpdate<E>::getSortValue() const {
    return sortValue;
}

template <typename E>
void FieldUpdate<E>::updateDelta(double delta, bool replaceUpdate) {
    if (replaceUpdate)
        this->delta = 0;

    this->delta += delta;

    updateSortValue(std::abs(this->delta));
}

template <typename E>
void FieldUpdate<E>::reset() {
    delta = 0.0;
}

template <typename E>
std::shared_ptr<Queue> FieldUpdate<E>::getQueue() const {
    return interceptor->getQueue();
}

template <typename E>
void FieldUpdate<E>::createQueueKey(Timestamp timestamp, int round) {
    queueKey = std::make_shared<FieldQueueKey>(round, getPhase(), sortValue, timestamp);
}

template <typename E>
void FieldUpdate<E>::process() {
    interceptor->process(shared_from_this());
}

template <typename E>
std::shared_ptr<ProcessingPhase> FieldUpdate<E>::getPhase() const {
    return phase;
}

template <typename E>
std::shared_ptr<E> FieldUpdate<E>::getElement() const {
    return std::dynamic_pointer_cast<E>(interceptor->getField()->getObject());
}

template <typename E>
std::shared_ptr<QueueInterceptor> FieldUpdate<E>::getInterceptor() const {
    return interceptor;
}

template <typename E>
std::string FieldUpdate<E>::toShortString() const {
    return " Round:" + std::to_string(getQueueKey()->getRound()) +
           " Delta:" + std::to_string(delta);
}

template <typename E>
std::string FieldUpdate<E>::toString() const {
    return getElement()->toString() + " Delta:" + std::to_string(delta) +
           " Field: " + interceptor->getField()->toString() +
           " Ref:" + interceptor->getField()->getObject()->toString();
}

// Explicit template instantiations (if needed)
template class FieldUpdate<Obj>;

