#!/usr/bin/env python3
"""
Test runner for activation tests
"""
import unittest
import sys
import os

# Add the project root to Python's module search path
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), "..")))

# Import test modules
from activation_test import ActivationTest
from conjunctive_activation_test import ConjunctiveActivationTest
from minimal_network_test import MinimalNetworkTest

def run_all_tests():
    """Run all activation tests"""
    # Create test suite
    suite = unittest.TestSuite()
    
    # Add tests from different test classes
    suite.addTest(unittest.makeSuite(ActivationTest))
    suite.addTest(unittest.makeSuite(ConjunctiveActivationTest))
    suite.addTest(unittest.makeSuite(MinimalNetworkTest))
    
    # Run tests
    runner = unittest.TextTestRunner(verbosity=2)
    result = runner.run(suite)
    
    return result.wasSuccessful()

if __name__ == '__main__':
    success = run_all_tests()
    sys.exit(0 if success else 1)