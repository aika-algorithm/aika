import unittest
import sys
import os

# Add the project root to Python's module search path
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), "..")))

import aika
from abstract_activation_test import AbstractActivationTest, TestBSType, TestUtils

class ActivationTest(AbstractActivationTest):
    
    def test_has_conflicting_binding_signals(self):
        """Test hasConflictingBindingSignals method"""
        # Create binding signals
        bs0 = aika.network.BindingSignal(0, self.doc)
        bs1 = aika.network.BindingSignal(1, self.doc)
        
        # Create activation with binding signal A -> bs0
        binding_signals = {self.BSType_A: bs0}
        activation = aika.network.ConjunctiveActivation(
            self.activation_def,
            None,  # parent
            1,     # id
            self.neuron,
            self.doc,
            binding_signals
        )
        
        # Test 1: Same binding signal should not conflict
        target_signals = {self.BSType_A: bs0}
        self.assertFalse(activation.hasConflictingBindingSignals(target_signals))
        
        # Test 2: Additional binding signal should not conflict
        target_signals = {self.BSType_A: bs0, self.BSType_B: bs1}
        self.assertFalse(activation.hasConflictingBindingSignals(target_signals))
        
        # Test 3: Different binding signal for same type should conflict
        target_signals = {self.BSType_A: bs1, self.BSType_B: bs0}
        self.assertTrue(activation.hasConflictingBindingSignals(target_signals))
    
    def test_has_new_binding_signals(self):
        """Test hasNewBindingSignals method"""
        # Create binding signals
        bs0 = aika.network.BindingSignal(0, self.doc)
        bs1 = aika.network.BindingSignal(1, self.doc)
        
        # Create activation with binding signal A -> bs0
        binding_signals = {self.BSType_A: bs0}
        activation = aika.network.ConjunctiveActivation(
            self.activation_def,
            None,  # parent
            1,     # id
            self.neuron,
            self.doc,
            binding_signals
        )
        
        # Test 1: Additional binding signal should be detected as new
        target_signals = {self.BSType_A: bs0, self.BSType_B: bs1}
        self.assertTrue(activation.hasNewBindingSignals(target_signals))
        
        # Test 2: Same binding signals should not be detected as new
        target_signals = {self.BSType_A: bs0}
        self.assertFalse(activation.hasNewBindingSignals(target_signals))
    
    def test_branch(self):
        """Test branch method"""
        # Create binding signals
        bs0 = aika.network.BindingSignal(0, self.doc)
        bs1 = aika.network.BindingSignal(1, self.doc)
        
        # Create parent activation
        parent_binding_signals = {self.BSType_A: bs0}
        parent_activation = aika.network.ConjunctiveActivation(
            self.activation_def,
            None,  # parent
            1,     # id
            self.neuron,
            self.doc,
            parent_binding_signals
        )
        
        # Create child activation through branching
        child_binding_signals = {self.BSType_B: bs1}
        child_activation = parent_activation.branch(child_binding_signals)
        
        # Verify parent-child relationship
        self.assertEqual(parent_activation, child_activation.getParent())
        
        # Verify child has the new binding signal
        child_signals = child_activation.getBindingSignals()
        self.assertEqual(1, len(child_signals))
        self.assertEqual(bs1, child_activation.getBindingSignal(self.BSType_B))
    
    def test_collect_linking_targets(self):
        """Test collectLinkingTargets method"""
        # This test would require more complex setup with multiple neurons
        # For now, we'll create a placeholder test
        pass
    
    def test_link_outgoing(self):
        """Test linkOutgoing method"""
        # This test would require synapse setup
        # For now, we'll create a placeholder test
        pass
    
    def test_propagate(self):
        """Test propagate method"""
        # This test would require synapse setup
        # For now, we'll create a placeholder test
        pass


if __name__ == '__main__':
    unittest.main()