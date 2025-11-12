#include "activation_test.h"
#include "link_latent_test.h"
#include <iostream>

int main() {
    std::cout << "Running C++ Network Tests..." << std::endl;
    
    try {
        // Run Activation Tests
        std::cout << "\n=== Running Activation Tests ===" << std::endl;
        ActivationTest activationTest;
        activationTest.runAllTests();
        
        // Run LinkLatent Tests - just one test to debug
        std::cout << "\n=== Running LinkLatent Tests ===" << std::endl;
        std::cout << "Creating LinkLatentTest object..." << std::endl;
        LinkLatentTest linkLatentTest;
        std::cout << "LinkLatentTest object created, calling testLinkLatentWithNullActivation()..." << std::endl;
        std::cout << "Running all LinkLatent tests..." << std::endl;
        linkLatentTest.runAllTests();
        std::cout << "All LinkLatent tests completed." << std::endl;
        
        std::cout << "\nAll tests completed successfully!" << std::endl;
        return 0;
    } catch (const std::exception& e) {
        std::cerr << "Test failed with exception: " << e.what() << std::endl;
        return 1;
    } catch (...) {
        std::cerr << "Test failed with unknown exception" << std::endl;
        return 1;
    }
}