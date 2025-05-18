#include "network/element_step.h"

ElementStep::ElementStep(Element* element) : element(element) {}

Queue* ElementStep::getQueue() const {
    return element->getQueue();
}

void ElementStep::createQueueKey(long timestamp, int round) {
    queueKey = new FiredQueueKey(round, getPhase(), element, timestamp);
}

Element* ElementStep::getElement() const {
    return element;
}

std::string ElementStep::toString() const {
    return "ElementStep for Element";
} 