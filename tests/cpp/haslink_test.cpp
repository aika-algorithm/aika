#include "network/synapse.h"
#include "network/types/synapse_type.h" 
#include "network/activation.h"
#include "fields/type_registry.h"
#include <iostream>
#include <cassert>

int main() {
    std::cout << "Testing hasLink method implementation..." << std::endl;
    
    // Create a simple test registry
    TypeRegistry registry;
    
    // Create a synapse type
    SynapseType synapseType(&registry, "TEST_SYNAPSE");
    
    // Create a synapse 
    Synapse synapse(&synapseType);
    
    // Test hasLink with null pointers (should return false)
    bool result1 = synapse.hasLink(nullptr, nullptr);
    assert(!result1);
    std::cout << "✅ hasLink(nullptr, nullptr) = " << result1 << std::endl;
    
    std::cout << "✅ hasLink method implementation test passed!" << std::endl;
    
    return 0;
}