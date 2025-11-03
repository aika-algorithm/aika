#include "network/fired.h"
#include "network/activation.h"
#include "fields/queue_key.h"
#include "network/fired_queue_key.h"
#include "network/binding_signal.h"
#include "network/context.h"
#include "network/linker.h"
#include <string>
#include <sstream>
#include <iomanip>

Fired::Fired(Activation* act) : Step(), act(act), net(0.0), sortValue(0) {}

void Fired::createQueueKey(long timestamp, int round) {
    queueKey = new FiredQueueKey(round, getPhase(), getElement(), timestamp);
}

void Fired::process() {
    Activation* act = getElement();
    
    act->setFired();

    // Only once the activation is fired, will it be visible to other neurons.
    for (BindingSignal* bs : act->getBindingSignals()) {
        bs->addActivation(act);
    }

    Linker::linkOutgoing(act);
}

void Fired::updateNet(double net) {
    this->net = net;
    // Simple conversion to int for sort value (handles approximate comparison)
    // The original Java uses ApproximateComparisonValueUtil
    sortValue = static_cast<int>(net * 1000.0); // Scale to preserve 3 decimals of precision
}

const Phase& Fired::getPhase() const {
    return Phase::FIRED;
}

Activation* Fired::getElement() const {
    return act;
}

Queue* Fired::getQueue() const {
    Context* ctx = act->getContext();
    return ctx->getQueue();
}

bool Fired::isQueued() const {
    return getIsQueued();
}

std::string Fired::toString() const {
    // Format the double with fixed precision (3 decimal places)
    std::ostringstream ss;
    ss << std::fixed << std::setprecision(3) << net;
    return getElement()->toString() + " net:" + ss.str();
} 