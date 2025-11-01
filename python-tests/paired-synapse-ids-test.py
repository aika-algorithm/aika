import unittest
import sys
import os

# Add the project root to Python's module search path
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), "..")))

import aika
import aika.fields as af
import aika.network as an

class PairedSynapseIdsTestCase(unittest.TestCase):
    """
    Test the paired synapse ID functionality in the Synapse class.
    This is a simpler test focusing only on the paired synapse ID fields
    without complex activation creation.
    """
    
    def setUp(self):
        """Set up test fixtures before each test method."""
        print("Setting up paired synapse IDs test...")
        self.registry = af.TypeRegistry()
        
        # Create basic neuron types
        self.input_builder = an.NeuronTypeBuilder(self.registry, "INPUT_NEURON")
        self.softmax_builder = an.NeuronTypeBuilder(self.registry, "SOFTMAX_NEURON")  
        self.output_builder = an.NeuronTypeBuilder(self.registry, "OUTPUT_NEURON")
        
        self.input_type = self.input_builder.build()
        self.softmax_type = self.softmax_builder.build()
        self.output_type = self.output_builder.build()
        
        print("Created neuron types: INPUT, SOFTMAX, OUTPUT")
        
    def test_paired_synapse_id_default_values(self):
        """Test that paired synapse ID fields have correct default values."""
        print("Testing paired synapse ID default values...")
        
        # Create synapse types  
        input_synapse_builder = an.SynapseTypeBuilder(self.registry, "INPUT_SYNAPSE")
        input_synapse_builder.setInput(self.input_type).setOutput(self.softmax_type)
        input_synapse_type = input_synapse_builder.build()
        
        # Flatten type hierarchy after creating synapse types
        self.registry.flattenTypeHierarchy()
        
        # Create synapse instance
        input_synapse = input_synapse_type.instantiate()
        
        # Test default values
        paired_input_id = input_synapse.getPairedInputSynapseId()
        paired_output_id = input_synapse.getPairedOutputSynapseId()
        
        self.assertEqual(paired_input_id, -1, "Default paired input synapse ID should be -1")
        self.assertEqual(paired_output_id, -1, "Default paired output synapse ID should be -1")
        
        print(f"✅ Default paired input synapse ID: {paired_input_id}")
        print(f"✅ Default paired output synapse ID: {paired_output_id}")
        
    def test_paired_synapse_id_setters_and_getters(self):
        """Test setting and getting paired synapse ID values."""
        print("Testing paired synapse ID setters and getters...")
        
        # Create synapse type
        output_synapse_builder = an.SynapseTypeBuilder(self.registry, "OUTPUT_SYNAPSE")
        output_synapse_builder.setInput(self.softmax_type).setOutput(self.output_type)
        output_synapse_type = output_synapse_builder.build()
        
        # Flatten type hierarchy
        self.registry.flattenTypeHierarchy()
        
        # Create synapse instance
        output_synapse = output_synapse_type.instantiate()
        
        # Test setting and getting paired input synapse ID
        output_synapse.setPairedInputSynapseId(123)
        paired_input_id = output_synapse.getPairedInputSynapseId()
        self.assertEqual(paired_input_id, 123, "Paired input synapse ID should be 123 after setting")
        
        # Test setting and getting paired output synapse ID
        output_synapse.setPairedOutputSynapseId(456)
        paired_output_id = output_synapse.getPairedOutputSynapseId()
        self.assertEqual(paired_output_id, 456, "Paired output synapse ID should be 456 after setting")
        
        # Test changing values
        output_synapse.setPairedInputSynapseId(789)
        output_synapse.setPairedOutputSynapseId(101112)
        
        self.assertEqual(output_synapse.getPairedInputSynapseId(), 789)
        self.assertEqual(output_synapse.getPairedOutputSynapseId(), 101112)
        
        # Test setting back to -1 (not set)
        output_synapse.setPairedInputSynapseId(-1)
        output_synapse.setPairedOutputSynapseId(-1)
        
        self.assertEqual(output_synapse.getPairedInputSynapseId(), -1)
        self.assertEqual(output_synapse.getPairedOutputSynapseId(), -1)
        
        print("✅ All paired synapse ID setter/getter operations work correctly")
        
    def test_paired_synapse_ids_with_pairing_config(self):
        """Test paired synapse IDs in context of pairing configs."""
        print("Testing paired synapse IDs with pairing configs...")
        
        # Create input synapse type
        input_synapse_builder = an.SynapseTypeBuilder(self.registry, "PAIRED_INPUT_SYNAPSE")
        input_synapse_builder.setInput(self.input_type).setOutput(self.softmax_type)
        input_synapse_type = input_synapse_builder.build()
        
        # Create output synapse type and pair it with input synapse
        output_synapse_builder = an.SynapseTypeBuilder(self.registry, "PAIRED_OUTPUT_SYNAPSE")
        output_synapse_builder.setInput(self.softmax_type).setOutput(self.output_type)
        output_synapse_builder.pairByBindingSignal(input_synapse_type, 0)  # Pair with binding signal slot 0
        output_synapse_type = output_synapse_builder.build()
        
        # Flatten type hierarchy
        self.registry.flattenTypeHierarchy()
        
        # Create synapse instances
        input_synapse = input_synapse_type.instantiate()
        output_synapse = output_synapse_type.instantiate()
        
        # Initially, paired IDs should be at default (-1)
        self.assertEqual(input_synapse.getPairedInputSynapseId(), -1)
        self.assertEqual(input_synapse.getPairedOutputSynapseId(), -1)
        self.assertEqual(output_synapse.getPairedInputSynapseId(), -1)
        self.assertEqual(output_synapse.getPairedOutputSynapseId(), -1)
        
        # Manually set up the pairing for demonstration
        # In a real scenario, this would be done during synapse creation/linking
        input_synapse.setSynapseId(1001)
        output_synapse.setSynapseId(2002)
        
        # Set up the paired references
        output_synapse.setPairedInputSynapseId(input_synapse.getSynapseId())  # Output synapse points to input synapse
        input_synapse.setPairedOutputSynapseId(output_synapse.getSynapseId())  # Input synapse points to output synapse
        
        # Verify the pairing
        self.assertEqual(output_synapse.getPairedInputSynapseId(), 1001)
        self.assertEqual(input_synapse.getPairedOutputSynapseId(), 2002)
        
        print(f"✅ Input synapse ID: {input_synapse.getSynapseId()}, paired output: {input_synapse.getPairedOutputSynapseId()}")
        print(f"✅ Output synapse ID: {output_synapse.getSynapseId()}, paired input: {output_synapse.getPairedInputSynapseId()}")
        print("✅ Paired synapse IDs work correctly with pairing configs")
        
    def test_softmax_use_case_setup(self):
        """Test the setup for softmax use case with paired synapses."""
        print("Testing softmax use case setup...")
        
        # This simulates the softmax scenario:
        # Input neurons connect to softmax neuron via input synapses
        # Softmax neuron connects to output neurons via output synapses  
        # The synapses are paired for routing binding signals
        
        # Create input synapse: INPUT → SOFTMAX
        input_syn_builder = an.SynapseTypeBuilder(self.registry, "SOFTMAX_INPUT")
        input_syn_builder.setInput(self.input_type).setOutput(self.softmax_type)
        input_syn_type = input_syn_builder.build()
        
        # Create output synapse: SOFTMAX → OUTPUT (paired to input synapse)
        output_syn_builder = an.SynapseTypeBuilder(self.registry, "SOFTMAX_OUTPUT")
        output_syn_builder.setInput(self.softmax_type).setOutput(self.output_type)
        output_syn_builder.pairByBindingSignal(input_syn_type, 1)  # Paired via binding signal slot 1
        output_syn_type = output_syn_builder.build()
        
        # Flatten type hierarchy
        self.registry.flattenTypeHierarchy()
        
        # Create synapse instances
        input_synapse = input_syn_type.instantiate()
        output_synapse = output_syn_type.instantiate()
        
        # Set unique synapse IDs
        input_synapse.setSynapseId(3001)
        output_synapse.setSynapseId(4002)
        
        # Set up pairing (this demonstrates the functionality we implemented)
        output_synapse.setPairedInputSynapseId(input_synapse.getSynapseId())
        
        # Verify the setup
        self.assertEqual(output_synapse.getPairedInputSynapseId(), 3001)
        
        print(f"✅ Softmax input synapse ID: {input_synapse.getSynapseId()}")
        print(f"✅ Softmax output synapse ID: {output_synapse.getSynapseId()}")  
        print(f"✅ Output synapse paired input ID: {output_synapse.getPairedInputSynapseId()}")
        print("✅ Softmax use case setup works correctly")
        
        # This is the foundation for the createInputKeyFromOutputCandidate method:
        # When we have an output synapse (4002) and want to look up the corresponding
        # input link in the softmax activation, we can use the paired input synapse ID (3001)
        # along with binding signal information to create the correct lookup key
        
    def tearDown(self):
        """Clean up after each test method."""
        print("Cleaning up paired synapse IDs test...")

if __name__ == '__main__':
    unittest.main(verbosity=2)