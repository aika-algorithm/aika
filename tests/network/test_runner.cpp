#include "activation_test.h"
#include <iostream>

int main() {
    std::cout << "Running C++ Activation Tests..." << std::endl;
    
    try {
        ActivationTest test;
        test.runAllTests();
        
        std::cout << "\nAll tests completed!" << std::endl;
        return 0;
    } catch (const std::exception& e) {
        std::cerr << "Test failed with exception: " << e.what() << std::endl;
        return 1;
    } catch (...) {
        std::cerr << "Test failed with unknown exception" << std::endl;
        return 1;
    }
}