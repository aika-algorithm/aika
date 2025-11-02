#ifndef NETWORK_ACTIVATIONS_PER_CONTEXT_H
#define NETWORK_ACTIVATIONS_PER_CONTEXT_H

#include <map>
#include <vector>

// Forward declarations
class Activation;
class Context;

/**
 * @class ActivationsPerContext
 * @brief Intermediate class to efficiently manage activations within a specific context.
 * 
 * This class maintains a map where binding signal token IDs (as std::vector<int>) are used
 * as keys to store and retrieve activations. This enables efficient lookup of activations
 * based on their binding signal patterns within a specific context.
 */
class ActivationsPerContext {
public:
    /**
     * @brief Constructor
     * @param context The context this instance manages activations for
     */
    explicit ActivationsPerContext(Context* context);
    
    /**
     * @brief Destructor
     */
    ~ActivationsPerContext();
    
    /**
     * @brief Add an activation to this context
     * @param activation The activation to add
     */
    void addActivation(Activation* activation);
    
    /**
     * @brief Remove an activation from this context
     * @param activation The activation to remove
     */
    void removeActivation(Activation* activation);
    
    /**
     * @brief Get activation by binding signal token IDs
     * @param tokenIds Vector of token IDs from binding signals
     * @return Pointer to activation if found, nullptr otherwise
     */
    Activation* getActivation(const std::vector<int>& tokenIds) const;
    
    /**
     * @brief Check if this context has any activations
     * @return True if empty, false otherwise
     */
    bool isEmpty() const;
    
    /**
     * @brief Get the number of activations in this context
     * @return Number of activations
     */
    size_t size() const;
    
    /**
     * @brief Get the context this instance manages
     * @return Pointer to the context
     */
    Context* getContext() const;

private:
    /**
     * @brief Create token ID vector from activation's binding signals
     * @param activation The activation to extract token IDs from
     * @return Vector of token IDs
     */
    std::vector<int> createTokenIdVector(Activation* activation) const;
    
    Context* context;
    std::map<std::vector<int>, Activation*> activationsByTokenIds;
};

#endif // NETWORK_ACTIVATIONS_PER_CONTEXT_H