#ifndef NETWORK_ACTIVATIONS_PER_CONTEXT_H
#define NETWORK_ACTIVATIONS_PER_CONTEXT_H

#include <map>
#include <vector>
#include <set>

// Forward declarations
class Activation;
class Context;

/**
 * @class ActivationsPerContext
 * @brief Intermediate class to efficiently manage activations within a specific context.
 * 
 * This class maintains a map where activation IDs are used as keys to store and retrieve
 * activations. This enables efficient lookup of activations within a specific context.
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
     * @brief Get activation by activation ID
     * @param activationId The ID of the activation to find
     * @return Pointer to activation if found, nullptr otherwise
     */
    Activation* getActivation(int activationId) const;
    
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
    
    /**
     * @brief Get all activations in this context
     * @return Set of all activations
     */
    std::set<Activation*> getActivations() const;

private:
    Context* context;
    std::map<int, Activation*> activationsById;
};

#endif // NETWORK_ACTIVATIONS_PER_CONTEXT_H