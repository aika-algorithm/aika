#include "network/document.h"
#include "network/activation.h"
#include "network/model.h"

Document::Document(Model* m, int length) : model(m), length(length), activationIdCounter(0), isStale(false) {
    id = model->createThoughtId();
    absoluteBeginChar = model->getN();
    model->registerDocument(this);
}

Document::~Document() {
    // Clean up activations and binding signals
    for (auto& pair : activations) {
        delete pair.second;
    }
    for (auto& pair : bindingSignals) {
        delete pair.second;
    }
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

Model* Document::getModel() {
    return model;
}

Config* Document::getConfig() {
    return model->getConfig();
}

Step* Document::getCurrentStep() {
    return currentStep;
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

Queue* Document::getQueue() {
    return this;
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