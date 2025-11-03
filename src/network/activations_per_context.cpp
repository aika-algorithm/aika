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
    activationsByTokenIds.clear();
}

void ActivationsPerContext::addActivation(Activation* activation) {
    if (!activation) {
        return;
    }
    
    std::vector<int> tokenIds = createTokenIdVector(activation);
    activationsByTokenIds[tokenIds] = activation;
}

void ActivationsPerContext::removeActivation(Activation* activation) {
    if (!activation) {
        return;
    }
    
    std::vector<int> tokenIds = createTokenIdVector(activation);
    activationsByTokenIds.erase(tokenIds);
}

Activation* ActivationsPerContext::getActivation(const std::vector<int>& tokenIds) const {
    auto it = activationsByTokenIds.find(tokenIds);
    return (it != activationsByTokenIds.end()) ? it->second : nullptr;
}

bool ActivationsPerContext::isEmpty() const {
    return activationsByTokenIds.empty();
}

size_t ActivationsPerContext::size() const {
    return activationsByTokenIds.size();
}

Context* ActivationsPerContext::getContext() const {
    return context;
}

std::vector<int> ActivationsPerContext::createTokenIdVector(Activation* activation) const {
    std::vector<int> tokenIds;
    
    if (!activation) {
        return tokenIds;
    }
    
    try {
        // Get binding signals from activation
        std::map<int, BindingSignal*> bindingSignals = activation->getBindingSignals();
        
        // Extract token IDs and sort them for consistent key generation
        for (const auto& pair : bindingSignals) {
            BindingSignal* bindingSignal = pair.second;
            if (bindingSignal) {
                tokenIds.push_back(bindingSignal->getTokenId());
            }
        }
        
        // Sort token IDs for consistent key generation
        std::sort(tokenIds.begin(), tokenIds.end());
        
    } catch (const std::exception& e) {
        // In case of any exception, return empty vector
        tokenIds.clear();
    } catch (...) {
        // In case of any unknown exception, return empty vector
        tokenIds.clear();
    }
    
    return tokenIds;
}