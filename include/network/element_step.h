#ifndef NETWORK_ELEMENT_STEP_H
#define NETWORK_ELEMENT_STEP_H

#include "fields/step.h"
#include "network/element.h"
#include "fields/queue_provider.h"
#include "network/fired_queue_key.h"

class ElementStep : public Step {
public:
    ElementStep(Element* element);
    Queue* getQueue() const override;
    void createQueueKey(long timestamp, int round) override;
    virtual const ProcessingPhase& getPhase() const override = 0; // Make this pure virtual for derived classes
    Element* getElement() const;
    std::string toString() const;

private:
    Element* element;
};

#endif // NETWORK_ELEMENT_STEP_H 