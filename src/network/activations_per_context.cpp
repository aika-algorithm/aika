#include "network/activations_per_context.h"
#include "network/activation.h"
#include "network/context.h"
#include "network/binding_signal.h"
#include <algorithm>

ActivationsPerContext::ActivationsPerContext(Context* context)
    : context(context) {
}

ActivationsPerContext::~ActivationsPerContext() {
    // Clear the map - activations themselves are managed by Context
    activationsById.clear();
}

void ActivationsPerContext::addActivation(Activation* activation) {
    if (!activation) {
        return;
    }
    
    activationsById[activation->getId()] = activation;
}

void ActivationsPerContext::removeActivation(Activation* activation) {
    if (!activation) {
        return;
    }
    
    activationsById.erase(activation->getId());
}

Activation* ActivationsPerContext::getActivation(int activationId) const {
    auto it = activationsById.find(activationId);
    return (it != activationsById.end()) ? it->second : nullptr;
}

bool ActivationsPerContext::isEmpty() const {
    return activationsById.empty();
}

size_t ActivationsPerContext::size() const {
    return activationsById.size();
}

Context* ActivationsPerContext::getContext() const {
    return context;
}

std::set<Activation*> ActivationsPerContext::getActivations() const {
    std::set<Activation*> result;
    for (const auto& pair : activationsById) {
        if (pair.second) {
            result.insert(pair.second);
        }
    }
    return result;
}
