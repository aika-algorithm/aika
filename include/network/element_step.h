#ifndef NETWORK_ELEMENT_STEP_H
#define NETWORK_ELEMENT_STEP_H

#include "network/step.h"
#include "element.h"
#include "queue_provider.h"
#include "fired_queue_key.h"

class ElementStep : public Step {
public:
    ElementStep(Element* element);
    Queue* getQueue() override;
    void createQueueKey(Timestamp timestamp, int round) override;
    Element* getElement();
    std::string toString() const override;

private:
    Element* element;
};

#endif // NETWORK_ELEMENT_STEP_H 