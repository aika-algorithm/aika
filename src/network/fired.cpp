#include "network/fired.h"
#include "network/activation.h"
#include "network/field_queue_key.h"
#include "network/approximate_comparison_value_util.h"
#include "network/string_utils.h"

Fired::Fired(Activation* act) : act(act), net(0.0), sortValue(0) {}

void Fired::createQueueKey(Timestamp timestamp, int round) {
    queueKey = new FieldQueueKey(round, getPhase(), sortValue, timestamp);
}

void Fired::process() {
    act->setFired();

    // Only once the activation is fired, will it be visible to other neurons.
    for (auto& bs : act->getBindingSignals()) {
        bs.second->addActivation(act);
    }

    act->linkOutgoing();
}

void Fired::updateNet(double net) {
    this->net = net;
    sortValue = ApproximateComparisonValueUtil::convert(net);
}

Phase Fired::getPhase() {
    return Phase::FIRED;
}

Activation* Fired::getElement() {
    return act;
}

Queue* Fired::getQueue() {
    return act->getDocument();
}

std::string Fired::toString() {
    return std::to_string(getElement()) + " net:" + StringUtils::doubleToString(net);
} 