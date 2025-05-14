#ifndef NETWORK_ELEMENT_STEP_H
#define NETWORK_ELEMENT_STEP_H

#include "network/step.h"
#include "element.h"
#include "queue_provider.h"
#include "fired_queue_key.h"

class ElementStep : public Step {
public:
    ElementStep(Element* element);
    Queue* getQueue() const override;
    void createQueueKey(long timestamp, int round) override;
    virtual const ProcessingPhase& getPhase() const override = 0; // Make this pure virtual for derived classes
    Element* getElement();
    std::string toString() const override;

private:
    Element* element;
};

#endif // NETWORK_ELEMENT_STEP_H 