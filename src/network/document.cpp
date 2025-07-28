#include "network/document.h"
#include "network/activation.h"
#include "network/model.h"

Document::Document(Model* m) : model(m), activationIdCounter(0), isStale(false) {
    id = model->createThoughtId();
    absoluteBeginChar = model->getN();
    model->registerDocument(this);
}

Document::~Document() {
    std::cout << "~Document begin" << std::endl;
    // Clean up activations and binding signals
    for (auto& pair : activations) {
        std::cout << "~Document act:" << pair.second << std::endl;
        delete pair.second;
    }
    std::cout << "~Document middle" << std::endl;
    for (auto& pair : bindingSignals) {
        delete pair.second;
    }
    std::cout << "~Document end" << std::endl;
}

long Document::getId() const {
    return id;
}

long Document::getTimeout() const {
    return getConfig()->getTimeout();
}

void Document::process(std::function<bool(Step*)> filter) {
    Queue::process(filter);
    if (model->getConfig()->isCountingEnabled()) {
        model->addToN(length);
    }
}

Model* Document::getModel() const {
    return model;
}

Config* Document::getConfig() const {
    return model->getConfig();
}

Step* Document::getCurrentStep() {
    // We need to work around the private access to currentStep
    // Check for any available information in the queue entries
    auto entries = getQueueEntries();
    if (!entries.empty()) {
        return entries[0]; // Return the first step in the queue as an approximation
    }
    return nullptr; // Return null if no steps are available
}

void Document::addActivation(Activation* act) {
    activations[act->getId()] = act;
}

std::set<Activation*> Document::getActivations() {
    std::set<Activation*> result;
    for (const auto& pair : activations) {
        result.insert(pair.second);
    }
    return result;
}

Activation* Document::getActivationByNeuron(Neuron* outputNeuron) {
    for (const auto& act : getActivations()) {
        if (act->getNeuron() == outputNeuron) {
            return act;
        }
    }
    return nullptr;
}

int Document::createActivationId() {
    return activationIdCounter++;
}

void Document::disconnect() {
    model->deregisterDocument(this);
    isStale = true;
}

Queue* Document::getQueue() const {
    return const_cast<Document*>(this);
}

Activation* Document::addToken(Neuron* n, BSType* bsType, int tokenId) {
    BindingSignal* bs = getOrCreateBindingSignal(tokenId);
    return n->createActivation(nullptr, this, {{bsType, bs}});
}

BindingSignal* Document::getOrCreateBindingSignal(int tokenId) {
    if (bindingSignals.find(tokenId) == bindingSignals.end()) {
        bindingSignals[tokenId] = new BindingSignal(tokenId, this);
    }
    return bindingSignals[tokenId];
}

BindingSignal* Document::getBindingSignal(int tokenId) {
    return bindingSignals[tokenId];
}

std::string Document::toString() const {
    return "Id:" + std::to_string(id);
} 