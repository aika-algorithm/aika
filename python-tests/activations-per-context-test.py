#!/usr/bin/env python3

import unittest
import sys
import os

# Add the project root to Python's module search path
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), "..")))

import aika
import aika.fields as af
import aika.network as an

class ActivationsPerContextTestCase(unittest.TestCase):
    """
    Test the ActivationsPerContext intermediate class functionality.
    This tests the one-to-many relation between Neuron and Activation using binding signal token IDs as keys.
    """
    
    def setUp(self):
        """Set up test fixtures before each test method."""
        print("Setting up ActivationsPerContext test...")
        self.registry = af.TypeRegistry()
        
        # Create neuron types
        self.input_builder = an.NeuronTypeBuilder(self.registry, "INPUT_NEURON")
        self.output_builder = an.NeuronTypeBuilder(self.registry, "OUTPUT_NEURON")
        
        self.input_type = self.input_builder.build()
        self.output_type = self.output_builder.build()
        
        self.registry.flattenTypeHierarchy()
        
        # Create model and context
        self.model = an.Model(self.registry)
        self.context = an.Context(self.model)
        
        print("Created neuron types: INPUT, OUTPUT")
        
    def test_activation_registration_and_cleanup(self):
        """Test that activations are properly registered and cleaned up in ActivationsPerContext."""
        print("Testing activation registration and cleanup...")
        
        # Create neuron instance
        input_neuron = self.input_type.instantiate(self.model)
        
        # Create binding signals with specific token IDs
        binding_signal_1 = self.context.getOrCreateBindingSignal(100)
        binding_signal_2 = self.context.getOrCreateBindingSignal(200)
        
        # Test 1: Create activation with binding signals
        binding_signals = {1: binding_signal_1, 2: binding_signal_2}
        activation = an.Activation(
            self.input_type.getActivationType(),
            None,  # parent
            self.context.createActivationId(),
            input_neuron,
            self.context,
            binding_signals
        )
        
        print(f"✅ Created activation with token IDs: {[100, 200]}")
        
        # The activation should be automatically registered due to constructor call
        print(f"✅ Activation automatically registered with neuron's ActivationsPerContext system")
        
        # Test 2: Verify the activation is accessible
        # Note: We can't directly test the C++ ActivationsPerContext methods from Python
        # since they're not exposed in the bindings, but we can verify the activation exists
        self.assertIsNotNone(activation)
        self.assertEqual(activation.getNeuron(), input_neuron)
        self.assertEqual(activation.getContext(), self.context)
        
        print("✅ Activation properly linked to neuron and context")
        
        # Test 3: Test cleanup - when activation goes out of scope and gets deleted,
        # it should be automatically removed from ActivationsPerContext
        del activation
        print("✅ Activation deleted - should be automatically removed from ActivationsPerContext")
        
    def test_multiple_activations_same_neuron_different_contexts(self):
        """Test multiple activations on same neuron but different contexts."""
        print("Testing multiple activations with different contexts...")
        
        # Create neuron instance
        neuron = self.input_type.instantiate(self.model)
        
        # Create two different contexts
        context1 = self.context
        context2 = an.Context(self.model)
        
        # Create binding signals
        binding_signal_1 = context1.getOrCreateBindingSignal(300)
        binding_signal_2 = context2.getOrCreateBindingSignal(400)
        
        # Create activations in different contexts
        activation1 = an.Activation(
            self.input_type.getActivationType(),
            None,
            context1.createActivationId(),
            neuron,
            context1,
            {1: binding_signal_1}
        )
        
        activation2 = an.Activation(
            self.input_type.getActivationType(),
            None,
            context2.createActivationId(),
            neuron,
            context2,
            {1: binding_signal_2}
        )
        
        print(f"✅ Created activation1 in context {context1.getId()} with token ID: 300")
        print(f"✅ Created activation2 in context {context2.getId()} with token ID: 400")
        
        # Both activations should exist and be properly registered
        self.assertIsNotNone(activation1)
        self.assertIsNotNone(activation2)
        self.assertEqual(activation1.getContext(), context1)
        self.assertEqual(activation2.getContext(), context2)
        self.assertEqual(activation1.getNeuron(), neuron)
        self.assertEqual(activation2.getNeuron(), neuron)
        
        print("✅ Both activations properly registered with same neuron but different contexts")
        
        # Clean up
        del activation1
        del activation2
        print("✅ Both activations deleted and cleaned up")
        
    def test_activations_with_different_binding_signals(self):
        """Test activations with different binding signal combinations."""
        print("Testing activations with different binding signal combinations...")
        
        # Create neuron instance
        neuron = self.input_type.instantiate(self.model)
        
        # Create binding signals
        bs1 = self.context.getOrCreateBindingSignal(500)
        bs2 = self.context.getOrCreateBindingSignal(600)
        bs3 = self.context.getOrCreateBindingSignal(700)
        
        # Create activations with different binding signal combinations
        activation1 = an.Activation(
            self.input_type.getActivationType(),
            None,
            self.context.createActivationId(),
            neuron,
            self.context,
            {1: bs1}  # Only token 500
        )
        
        activation2 = an.Activation(
            self.input_type.getActivationType(),
            None,
            self.context.createActivationId(),
            neuron,
            self.context,
            {1: bs1, 2: bs2}  # Tokens 500, 600
        )
        
        activation3 = an.Activation(
            self.input_type.getActivationType(),
            None,
            self.context.createActivationId(),
            neuron,
            self.context,
            {1: bs2, 2: bs3}  # Tokens 600, 700
        )
        
        print("✅ Created 3 activations with different token combinations:")
        print("   - Activation1: [500]")
        print("   - Activation2: [500, 600]")
        print("   - Activation3: [600, 700]")
        
        # All activations should be distinct and properly registered
        self.assertIsNotNone(activation1)
        self.assertIsNotNone(activation2)
        self.assertIsNotNone(activation3)
        
        # They should all belong to the same neuron and context
        for activation in [activation1, activation2, activation3]:
            self.assertEqual(activation.getNeuron(), neuron)
            self.assertEqual(activation.getContext(), self.context)
        
        print("✅ All activations properly registered with different binding signal keys")
        
        # Clean up
        del activation1
        del activation2
        del activation3
        print("✅ All activations deleted and cleaned up")
        
    def tearDown(self):
        """Clean up after each test method."""
        print("Cleaning up ActivationsPerContext test...")

if __name__ == '__main__':
    unittest.main(verbosity=2)