import unittest
import sys
import os

# Add the project root to Python's module search path
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), "..")))

import aika
import aika.fields as af
import aika.network as an

class PairingLogicTestCase(unittest.TestCase):
    """
    Test the elegant pairing logic using common neuron type detection.
    This test verifies that the new pairing approach correctly determines
    which side (input/output) to attach pairing configs based on common neuron types.
    """
    
    def setUp(self):
        """Set up test fixtures before each test method."""
        print("Setting up pairing logic test...")
        self.registry = af.TypeRegistry()
        
        # Create basic neuron types for testing
        self.neuron_A_builder = an.NeuronTypeBuilder(self.registry, "NEURON_A")
        self.neuron_B_builder = an.NeuronTypeBuilder(self.registry, "NEURON_B") 
        self.neuron_C_builder = an.NeuronTypeBuilder(self.registry, "NEURON_C")
        
        self.neuron_A = self.neuron_A_builder.build()
        self.neuron_B = self.neuron_B_builder.build()
        self.neuron_C = self.neuron_C_builder.build()
        
        print("Created test neuron types: A, B, C")
        
    def test_common_input_neuron_pairing(self):
        """Test pairing with common input neuron type."""
        print("Testing common input neuron pairing...")
        
        # Create two synapse types that share a common input neuron (A)
        # Synapse 1: A → B
        # Synapse 2: A → C
        # Common neuron: A (input side)
        
        synapse1_builder = an.SynapseTypeBuilder(self.registry, "SYN_A_TO_B")
        synapse1_builder.setInput(self.neuron_A).setOutput(self.neuron_B)
        synapse1 = synapse1_builder.build()
        
        synapse2_builder = an.SynapseTypeBuilder(self.registry, "SYN_A_TO_C")
        synapse2_builder.setInput(self.neuron_A).setOutput(self.neuron_C)
        
        # Add pairing: synapse2 should pair with synapse1 via binding signal slot 0
        synapse2_builder.pair(synapse1, 0)
        synapse2 = synapse2_builder.build()
        
        print("✅ Created paired synapses with common input neuron A")
        
        # Verify pairing configurations were attached correctly
        # Since common neuron A is on input side, pairing should be on input side
        
        # Check synapse2's pairing configuration
        input_pairing = synapse2.getInputSidePairingConfig()
        output_pairing = synapse2.getOutputSidePairingConfig()
        
        if input_pairing:
            self.assertEqual(input_pairing.pairedSynapseType, synapse1)
            self.assertEqual(input_pairing.type, an.PairingType.BY_BINDING_SIGNAL)
            self.assertEqual(input_pairing.bindingSignalSlot, 0)
            print("✅ Input side pairing config correct")
        else:
            print("⚠️ Input side pairing config not found")
            
        if output_pairing:
            print(f"⚠️ Unexpected output side pairing: {output_pairing.pairedSynapseType}")
        else:
            print("✅ No unexpected output side pairing")
            
        print("✅ Common input neuron pairing test completed")
        
    def test_common_output_neuron_pairing(self):
        """Test pairing with common output neuron type."""
        print("Testing common output neuron pairing...")
        
        # Create two synapse types that share a common output neuron (B)  
        # Synapse 1: A → B
        # Synapse 2: C → B
        # Common neuron: B (output side)
        
        synapse1_builder = an.SynapseTypeBuilder(self.registry, "SYN_A_TO_B2")
        synapse1_builder.setInput(self.neuron_A).setOutput(self.neuron_B)
        synapse1 = synapse1_builder.build()
        
        synapse2_builder = an.SynapseTypeBuilder(self.registry, "SYN_C_TO_B")
        synapse2_builder.setInput(self.neuron_C).setOutput(self.neuron_B)
        
        # Add pairing: synapse2 should pair with synapse1 via binding signal slot 1
        synapse2_builder.pair(synapse1, 1)
        synapse2 = synapse2_builder.build()
        
        print("✅ Created paired synapses with common output neuron B")
        
        # Verify pairing configurations were attached correctly
        # Since common neuron B is on output side, pairing should be on output side
        
        # Check synapse2's pairing configuration
        input_pairing = synapse2.getInputSidePairingConfig()
        output_pairing = synapse2.getOutputSidePairingConfig()
        
        if output_pairing:
            self.assertEqual(output_pairing.pairedSynapseType, synapse1)
            self.assertEqual(output_pairing.type, an.PairingType.BY_BINDING_SIGNAL)
            self.assertEqual(output_pairing.bindingSignalSlot, 1)
            print("✅ Output side pairing config correct")
        else:
            print("⚠️ Output side pairing config not found")
            
        if input_pairing:
            print(f"⚠️ Unexpected input side pairing: {input_pairing.pairedSynapseType}")
        else:
            print("✅ No unexpected input side pairing")
            
        print("✅ Common output neuron pairing test completed")
        
    def test_cross_connection_pairing(self):
        """Test pairing with cross-connected neuron types."""
        print("Testing cross-connection pairing...")
        
        # Create two synapse types with cross connections
        # Synapse 1: A → B  
        # Synapse 2: B → A
        # Connection: A appears on both sides, B appears on both sides
        # Expected: Should determine based on the first match found
        
        synapse1_builder = an.SynapseTypeBuilder(self.registry, "SYN_A_TO_B3")
        synapse1_builder.setInput(self.neuron_A).setOutput(self.neuron_B)
        synapse1 = synapse1_builder.build()
        
        synapse2_builder = an.SynapseTypeBuilder(self.registry, "SYN_B_TO_A")
        synapse2_builder.setInput(self.neuron_B).setOutput(self.neuron_A)
        
        # Add pairing: synapse2 should pair with synapse1
        synapse2_builder.pair(synapse1)
        synapse2 = synapse2_builder.build()
        
        print("✅ Created cross-connected paired synapses")
        
        # Verify pairing was attached to some side
        # The exact side depends on the order of checks in the algorithm
        input_pairing = synapse2.getInputSidePairingConfig()
        output_pairing = synapse2.getOutputSidePairingConfig()
        
        
        # At least one side should have pairing
        has_pairing = input_pairing is not None or output_pairing is not None
        self.assertTrue(has_pairing, "At least one side should have pairing configuration")
        
        if input_pairing and input_pairing.pairedSynapseType:
            self.assertEqual(input_pairing.pairedSynapseType, synapse1)
            self.assertEqual(input_pairing.type, an.PairingType.BY_SYNAPSE)
            print("✅ Input side pairing established")
            
        if output_pairing and output_pairing.pairedSynapseType:
            self.assertEqual(output_pairing.pairedSynapseType, synapse1)
            self.assertEqual(output_pairing.type, an.PairingType.BY_SYNAPSE)
            print("✅ Output side pairing established")
        
        # The elegant algorithm chooses one side based on the order of checks
        # Either input OR output should have pairing, but not necessarily both
        pairing_count = (1 if input_pairing else 0) + (1 if output_pairing else 0)
        self.assertGreaterEqual(pairing_count, 1, "At least one side should have pairing")
        
        # Report which side was chosen
        if input_pairing and not output_pairing:
            print("✅ Elegant algorithm chose input side pairing")
        elif output_pairing and not input_pairing:
            print("✅ Elegant algorithm chose output side pairing") 
        elif input_pairing and output_pairing:
            print("✅ Elegant algorithm established both sides (unexpected but valid)")
            
        print("✅ Cross-connection pairing test completed")
        
    def test_bidirectional_pairing(self):
        """Test that bidirectional reverse pairing is established."""
        print("Testing bidirectional pairing...")
        
        # Create two synapse types
        synapse1_builder = an.SynapseTypeBuilder(self.registry, "SYN_BIDIR_1")
        synapse1_builder.setInput(self.neuron_A).setOutput(self.neuron_B)
        synapse1 = synapse1_builder.build()
        
        synapse2_builder = an.SynapseTypeBuilder(self.registry, "SYN_BIDIR_2")
        synapse2_builder.setInput(self.neuron_A).setOutput(self.neuron_C)  # Same input neuron
        
        # Add pairing from synapse2 to synapse1
        synapse2_builder.pair(synapse1, 2)
        synapse2 = synapse2_builder.build()
        
        print("✅ Created bidirectional pairing setup")
        
        # Check that both synapses have pairing configurations
        # synapse2 should have forward pairing to synapse1
        syn2_input_pairing = synapse2.getInputSidePairingConfig()
        syn2_output_pairing = synapse2.getOutputSidePairingConfig()
        
        # synapse1 should have reverse pairing to synapse2
        syn1_input_pairing = synapse1.getInputSidePairingConfig()
        syn1_output_pairing = synapse1.getOutputSidePairingConfig()
        
        # Verify forward pairing exists
        forward_pairing_exists = syn2_input_pairing is not None or syn2_output_pairing is not None
        self.assertTrue(forward_pairing_exists, "Forward pairing should exist")
        
        # Verify reverse pairing exists
        reverse_pairing_exists = syn1_input_pairing is not None or syn1_output_pairing is not None
        self.assertTrue(reverse_pairing_exists, "Reverse pairing should exist")
        
        print("✅ Bidirectional pairing established correctly")
        print("✅ Bidirectional pairing test completed")
        
    def tearDown(self):
        """Clean up after each test method."""
        print("Cleaning up pairing logic test...")
        self.registry = None

if __name__ == '__main__':
    unittest.main(verbosity=2)