#include "network/element_step.h"

ElementStep::ElementStep(Element* element) : element(element) {}

Queue* ElementStep::getQueue() {
    return element->getQueue();
}

void ElementStep::createQueueKey(Timestamp timestamp, int round) {
    queueKey = new FiredQueueKey(round, getPhase(), element, timestamp);
}

Element* ElementStep::getElement() {
    return element;
}

std::string ElementStep::toString() const {
    return element->toString();
} 