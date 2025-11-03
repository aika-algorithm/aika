#include "network/context.h"
#include "network/activation.h"
#include "network/model.h"
#include "network/types/neuron_type.h"

Context::Context(Model* m) : model(m), activationIdCounter(0), isStale(false) {
    id = model->createContextId();
    model->registerContext(this);
}

Context::~Context() {
    std::cout << "~Context begin" << std::endl;
    
    // Clean up activations FIRST (they need to access binding signals during destruction)
    for (auto& pair : activations) {
        std::cout << "~Context act:" << pair.second << std::endl;
        if (pair.second != nullptr) {
            delete pair.second;
            pair.second = nullptr;
        }
    }
    std::cout << "~Context middle" << std::endl;
    
    // Then clean up binding signals (activations no longer reference them)
    for (auto& pair : bindingSignals) {
        std::cout << "~Context bs:" << pair.second << std::endl;
        if (pair.second != nullptr) {
            delete pair.second;
            pair.second = nullptr;
        }
    }
    std::cout << "~Context end" << std::endl;
}

long Context::getId() const {
    return id;
}

long Context::getTimeout() const {
    return getConfig()->getTimeout();
}

void Context::process(std::function<bool(Step*)> filter) {
    Queue::process(filter);
    if (model->getConfig()->isCountingEnabled()) {
        model->addToN(length);
    }
}

Model* Context::getModel() const {
    return model;
}

Config* Context::getConfig() const {
    return model->getConfig();
}

Step* Context::getCurrentStep() {
    // We need to work around the private access to currentStep
    // Check for any available information in the queue entries
    auto entries = getQueueEntries();
    if (!entries.empty()) {
        return entries[0]; // Return the first step in the queue as an approximation
    }
    return nullptr; // Return null if no steps are available
}

void Context::addActivation(Activation* act) {
    activations[act->getId()] = act;
}

void Context::removeActivation(Activation* act) {
    activations.erase(act->getId());
}

std::set<Activation*> Context::getActivations() {
    std::set<Activation*> result;
    for (const auto& pair : activations) {
        result.insert(pair.second);
    }
    return result;
}

Activation* Context::getActivationByNeuron(Neuron* outputNeuron) {
    for (const auto& act : getActivations()) {
        if (act->getNeuron() == outputNeuron) {
            return act;
        }
    }
    return nullptr;
}

int Context::createActivationId() {
    return activationIdCounter++;
}

void Context::disconnect() {
    model->deregisterContext(this);
    isStale = true;
}

Queue* Context::getQueue() const {
    return const_cast<Context*>(this);
}

Activation* Context::addToken(Neuron* n, int bsSlot, int tokenId) {
    BindingSignal* bs = getOrCreateBindingSignal(tokenId);
    
    // Create array and populate with binding signal at the specified slot
    // Create binding signals array using the neuron's method
    BindingSignal** bindingSignals = n->createBindingSignalArray();
    
    int numberOfBSSlots = n->getNumberOfBSSlots();
    
    if (bsSlot >= 0 && bsSlot < numberOfBSSlots) {
        bindingSignals[bsSlot] = bs;
    }
    
    return n->createActivation(nullptr, this, bindingSignals);
}

BindingSignal* Context::getOrCreateBindingSignal(int tokenId) {
    if (bindingSignals.find(tokenId) == bindingSignals.end()) {
        bindingSignals[tokenId] = new BindingSignal(tokenId, this);
    }
    return bindingSignals[tokenId];
}

BindingSignal* Context::getBindingSignal(int tokenId) {
    return bindingSignals[tokenId];
}

std::string Context::toString() const {
    return "Id:" + std::to_string(id);
} 