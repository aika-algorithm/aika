import unittest
import sys
import os

# Add the project root to Python's module search path
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), "..")))

import aika
import aika.fields as af
import aika.network as an

class SoftmaxInputKeyTestCase(unittest.TestCase):
    """
    Test the softmax InputKey generation functionality.
    This tests the new paired synapse ID fields and the createInputKeyFromOutputCandidate method.
    """
    
    def setUp(self):
        """Set up test fixtures before each test method."""
        print("Setting up softmax input key test...")
        self.registry = af.TypeRegistry()
        
        # Create basic neuron types for softmax scenario
        self.input_builder = an.NeuronTypeBuilder(self.registry, "INPUT_NEURON")
        self.softmax_builder = an.NeuronTypeBuilder(self.registry, "SOFTMAX_NEURON")  
        self.output_builder = an.NeuronTypeBuilder(self.registry, "OUTPUT_NEURON")
        
        self.input_type = self.input_builder.build()
        self.softmax_type = self.softmax_builder.build()
        self.output_type = self.output_builder.build()
        
        self.registry.flattenTypeHierarchy()
        
        print("Created neuron types: INPUT, SOFTMAX, OUTPUT")
        
    def test_paired_synapse_id_fields(self):
        """Test that paired synapse ID fields exist and work correctly."""
        print("Testing paired synapse ID fields...")
        
        # Create synapse types
        input_synapse_builder = an.SynapseTypeBuilder(self.registry, "INPUT_SYNAPSE")
        input_synapse_builder.setInput(self.input_type).setOutput(self.softmax_type)
        input_synapse_type = input_synapse_builder.build()
        
        output_synapse_builder = an.SynapseTypeBuilder(self.registry, "OUTPUT_SYNAPSE")
        output_synapse_builder.setInput(self.softmax_type).setOutput(self.output_type)
        # Pair the output synapse with the input synapse via binding signal
        output_synapse_builder.pairByBindingSignal(input_synapse_type, 0)
        output_synapse_type = output_synapse_builder.build()
        
        # Create synapse instances
        input_synapse = input_synapse_type.instantiate()
        output_synapse = output_synapse_type.instantiate()
        
        # Test default values
        input_paired_input = input_synapse.getPairedInputSynapseId()
        input_paired_output = input_synapse.getPairedOutputSynapseId()
        output_paired_input = output_synapse.getPairedInputSynapseId()
        output_paired_output = output_synapse.getPairedOutputSynapseId()
        
        # Default should be -1
        self.assertEqual(input_paired_input, -1, "Default paired input synapse ID should be -1")
        self.assertEqual(input_paired_output, -1, "Default paired output synapse ID should be -1")
        self.assertEqual(output_paired_input, -1, "Default paired input synapse ID should be -1")
        self.assertEqual(output_paired_output, -1, "Default paired output synapse ID should be -1")
        
        # Test setting values
        input_synapse.setPairedInputSynapseId(100)
        input_synapse.setPairedOutputSynapseId(200)
        output_synapse.setPairedInputSynapseId(300)
        output_synapse.setPairedOutputSynapseId(400)
        
        # Verify values were set
        self.assertEqual(input_synapse.getPairedInputSynapseId(), 100)
        self.assertEqual(input_synapse.getPairedOutputSynapseId(), 200)
        self.assertEqual(output_synapse.getPairedInputSynapseId(), 300)
        self.assertEqual(output_synapse.getPairedOutputSynapseId(), 400)
        
        print(f"✅ Input synapse paired IDs: {input_synapse.getPairedInputSynapseId()}, {input_synapse.getPairedOutputSynapseId()}")
        print(f"✅ Output synapse paired IDs: {output_synapse.getPairedInputSynapseId()}, {output_synapse.getPairedOutputSynapseId()}")
        print("✅ Paired synapse ID fields work correctly")
        
    def test_create_input_key_method_exists(self):
        """Test that the createInputKeyFromOutputCandidate method exists and is callable."""
        print("Testing createInputKeyFromOutputCandidate method existence...")
        
        # Create model and context
        model = an.Model(self.registry)
        context = an.Context(model)
        
        # Create neuron instances
        input_neuron = self.input_type.instantiate(model)
        softmax_neuron = self.softmax_type.instantiate(model)
        output_neuron = self.output_type.instantiate(model)
        
        # Create softmax activation
        softmax_activation = an.Activation(
            self.softmax_type.getActivationType(),
            None,  # parent
            context.createActivationId(),
            softmax_neuron,
            context,
            {}  # binding signals
        )
        
        # Create output activation
        output_activation = an.Activation(
            self.output_type.getActivationType(),
            None,  # parent
            context.createActivationId(),
            output_neuron,
            context,
            {}  # binding signals
        )
        
        # Create output synapse
        output_synapse_builder = an.SynapseTypeBuilder(self.registry, "TEST_OUTPUT_SYNAPSE")
        output_synapse_builder.setInput(self.softmax_type).setOutput(self.output_type)
        output_synapse_type = output_synapse_builder.build()
        output_synapse = output_synapse_type.instantiate()
        
        # Test that the method exists and is callable
        self.assertTrue(hasattr(softmax_activation, 'createInputKeyFromOutputCandidate'),
                       "createInputKeyFromOutputCandidate method should exist")
        
        # Call the method (should return empty key since no paired synapse ID is set)
        input_key = softmax_activation.createInputKeyFromOutputCandidate(output_synapse, output_activation)
        
        # Should return empty list since paired input synapse ID is -1 (not set)
        self.assertEqual(len(input_key), 0, "Should return empty key when no paired synapse ID is set")
        
        print("✅ createInputKeyFromOutputCandidate method exists and is callable")
        print(f"✅ Method returned: {input_key} (empty as expected)")
        
    def test_input_key_generation_basic(self):
        """Test basic input key generation functionality."""
        print("Testing basic input key generation...")
        
        # Create model and context
        model = an.Model(self.registry)
        context = an.Context(model)
        
        # Create neuron instances
        softmax_neuron = self.softmax_type.instantiate(model)
        output_neuron = self.output_type.instantiate(model)
        
        # Create softmax activation
        softmax_activation = an.Activation(
            self.softmax_type.getActivationType(),
            None,  # parent
            context.createActivationId(),
            softmax_neuron,
            context,
            {}  # binding signals
        )
        
        # Create output activation with some binding signals
        token = context.addToken("test_token")
        binding_signal = context.getOrCreateBindingSignal(token, 0)
        output_binding_signals = {0: binding_signal}
        
        output_activation = an.Activation(
            self.output_type.getActivationType(),
            None,  # parent
            context.createActivationId(),
            output_neuron,
            context,
            output_binding_signals
        )
        
        # Create output synapse with paired input synapse ID set
        output_synapse_builder = an.SynapseTypeBuilder(self.registry, "TEST_OUTPUT_SYNAPSE_WITH_PAIR")
        output_synapse_builder.setInput(self.softmax_type).setOutput(self.output_type)
        output_synapse_type = output_synapse_builder.build()
        output_synapse = output_synapse_type.instantiate()
        
        # Set paired input synapse ID
        output_synapse.setPairedInputSynapseId(42)
        
        # Generate input key
        input_key = softmax_activation.createInputKeyFromOutputCandidate(output_synapse, output_activation)
        
        # Should at least contain the paired synapse ID
        self.assertTrue(len(input_key) > 0, "Input key should not be empty when paired synapse ID is set")
        self.assertEqual(input_key[0], 42, "First element should be the paired input synapse ID")
        
        print(f"✅ Generated input key: {input_key}")
        print("✅ Basic input key generation works")
        
    def tearDown(self):
        """Clean up after each test method."""
        print("Cleaning up softmax input key test...")

if __name__ == '__main__':
    unittest.main(verbosity=2)