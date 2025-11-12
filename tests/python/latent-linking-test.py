import unittest
import sys
import os

# Add the project root to Python's module search path
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), "..")))

import aika
import aika.fields as af
import aika.network as an

class LatentLinkingTestCase(unittest.TestCase):
    """
    Test the allowLatentLinking field logic.
    This field should be set to true when both paired synapse types 
    share a common output neuron type.
    """
    
    def setUp(self):
        """Set up test fixtures before each test method."""
        print("Setting up latent linking test...")
        self.registry = af.TypeRegistry()
        
        # Create basic neuron types for testing
        self.neuron_A_builder = an.NeuronTypeBuilder(self.registry, "NEURON_A")
        self.neuron_B_builder = an.NeuronTypeBuilder(self.registry, "NEURON_B") 
        self.neuron_C_builder = an.NeuronTypeBuilder(self.registry, "NEURON_C")
        
        self.neuron_A = self.neuron_A_builder.build()
        self.neuron_B = self.neuron_B_builder.build()
        self.neuron_C = self.neuron_C_builder.build()
        
        print("Created test neuron types: A, B, C")
        
    def test_common_output_enables_latent_linking(self):
        """Test that common output neuron type enables latent linking on both synapse types."""
        print("Testing common output neuron enables latent linking...")
        
        # Create two synapse types that share a common output neuron (B)
        # Synapse 1: A → B
        # Synapse 2: C → B
        # Common output neuron: B -> should enable latent linking
        
        synapse1_builder = an.SynapseTypeBuilder(self.registry, "SYN_A_TO_B_LATENT")
        synapse1_builder.setInput(self.neuron_A).setOutput(self.neuron_B)
        synapse1 = synapse1_builder.build()
        
        # Check initial state (should be false before pairing)
        self.assertFalse(synapse1.getAllowLatentLinking(), "Initial allowLatentLinking should be false")
        
        synapse2_builder = an.SynapseTypeBuilder(self.registry, "SYN_C_TO_B_LATENT")
        synapse2_builder.setInput(self.neuron_C).setOutput(self.neuron_B)
        
        # Add pairing: synapse2 pairs with synapse1 (common output neuron B)
        synapse2_builder.pair(synapse1, 0)
        synapse2 = synapse2_builder.build()
        
        print("✅ Created paired synapses with common output neuron B")
        
        # Verify that both synapse types now have allowLatentLinking = true
        latent_linking_1 = synapse1.getAllowLatentLinking()
        latent_linking_2 = synapse2.getAllowLatentLinking()
        
        self.assertTrue(latent_linking_1, "Synapse 1 should have allowLatentLinking = true")
        self.assertTrue(latent_linking_2, "Synapse 2 should have allowLatentLinking = true")
        
        print(f"✅ Synapse 1 allowLatentLinking: {latent_linking_1}")
        print(f"✅ Synapse 2 allowLatentLinking: {latent_linking_2}")
        print("✅ Common output neuron enables latent linking test completed")
        
    def test_common_input_no_latent_linking(self):
        """Test that common input neuron type does NOT enable latent linking."""
        print("Testing common input neuron does NOT enable latent linking...")
        
        # Create two synapse types that share a common input neuron (A)
        # Synapse 1: A → B
        # Synapse 2: A → C
        # Common input neuron: A -> should NOT enable latent linking
        
        synapse1_builder = an.SynapseTypeBuilder(self.registry, "SYN_A_TO_B_NO_LATENT")
        synapse1_builder.setInput(self.neuron_A).setOutput(self.neuron_B)
        synapse1 = synapse1_builder.build()
        
        synapse2_builder = an.SynapseTypeBuilder(self.registry, "SYN_A_TO_C_NO_LATENT")
        synapse2_builder.setInput(self.neuron_A).setOutput(self.neuron_C)
        
        # Add pairing: synapse2 pairs with synapse1 (common input neuron A)
        synapse2_builder.pair(synapse1, 1)
        synapse2 = synapse2_builder.build()
        
        print("✅ Created paired synapses with common input neuron A")
        
        # Verify that both synapse types have allowLatentLinking = false
        latent_linking_1 = synapse1.getAllowLatentLinking()
        latent_linking_2 = synapse2.getAllowLatentLinking()
        
        self.assertFalse(latent_linking_1, "Synapse 1 should have allowLatentLinking = false")
        self.assertFalse(latent_linking_2, "Synapse 2 should have allowLatentLinking = false")
        
        print(f"✅ Synapse 1 allowLatentLinking: {latent_linking_1}")
        print(f"✅ Synapse 2 allowLatentLinking: {latent_linking_2}")
        print("✅ Common input neuron does NOT enable latent linking test completed")
        
    def test_no_common_neuron_no_latent_linking(self):
        """Test that no common neuron type does NOT enable latent linking."""
        print("Testing no common neuron does NOT enable latent linking...")
        
        # Create two synapse types with no common neurons
        # Synapse 1: A → B
        # Synapse 2: C → C (different input and output)
        # No common neurons -> should NOT enable latent linking
        
        synapse1_builder = an.SynapseTypeBuilder(self.registry, "SYN_A_TO_B_UNRELATED")
        synapse1_builder.setInput(self.neuron_A).setOutput(self.neuron_B)
        synapse1 = synapse1_builder.build()
        
        synapse2_builder = an.SynapseTypeBuilder(self.registry, "SYN_C_TO_C_UNRELATED")
        synapse2_builder.setInput(self.neuron_C).setOutput(self.neuron_C)
        
        # Add pairing: synapse2 pairs with synapse1 (no common neurons)
        synapse2_builder.pair(synapse1)
        synapse2 = synapse2_builder.build()
        
        print("✅ Created paired synapses with no common neurons")
        
        # Verify that both synapse types have allowLatentLinking = false
        latent_linking_1 = synapse1.getAllowLatentLinking()
        latent_linking_2 = synapse2.getAllowLatentLinking()
        
        self.assertFalse(latent_linking_1, "Synapse 1 should have allowLatentLinking = false")
        self.assertFalse(latent_linking_2, "Synapse 2 should have allowLatentLinking = false")
        
        print(f"✅ Synapse 1 allowLatentLinking: {latent_linking_1}")
        print(f"✅ Synapse 2 allowLatentLinking: {latent_linking_2}")
        print("✅ No common neuron does NOT enable latent linking test completed")
        
    def test_multiple_pairings_latent_linking(self):
        """Test latent linking behavior with multiple pairing configurations."""
        print("Testing multiple pairings latent linking...")
        
        # Create three synapse types:
        # Synapse 1: A → B  
        # Synapse 2: C → B (shares output B with synapse 1)
        # Synapse 3: A → C (shares input A with synapse 1, but no output sharing)
        
        synapse1_builder = an.SynapseTypeBuilder(self.registry, "SYN_MULTI_1")
        synapse1_builder.setInput(self.neuron_A).setOutput(self.neuron_B)
        synapse1 = synapse1_builder.build()
        
        # Synapse 2 pairs with synapse 1 (common output B)
        synapse2_builder = an.SynapseTypeBuilder(self.registry, "SYN_MULTI_2")
        synapse2_builder.setInput(self.neuron_C).setOutput(self.neuron_B)
        synapse2_builder.pair(synapse1, 0)
        synapse2 = synapse2_builder.build()
        
        # Synapse 3 pairs with synapse 1 (common input A, different outputs)
        synapse3_builder = an.SynapseTypeBuilder(self.registry, "SYN_MULTI_3")
        synapse3_builder.setInput(self.neuron_A).setOutput(self.neuron_C)
        synapse3_builder.pair(synapse1, 1)
        synapse3 = synapse3_builder.build()
        
        print("✅ Created three synapse types with mixed pairing scenarios")
        
        # Check latent linking results
        latent_linking_1 = synapse1.getAllowLatentLinking()
        latent_linking_2 = synapse2.getAllowLatentLinking()
        latent_linking_3 = synapse3.getAllowLatentLinking()
        
        # Synapse 1 and 2 should have latent linking enabled (common output B)
        self.assertTrue(latent_linking_1, "Synapse 1 should have allowLatentLinking = true (common output)")
        self.assertTrue(latent_linking_2, "Synapse 2 should have allowLatentLinking = true (common output)")
        
        # Synapse 3 should NOT have latent linking enabled (only common input, no common output)
        self.assertFalse(latent_linking_3, "Synapse 3 should have allowLatentLinking = false (no common output)")
        
        print(f"✅ Synapse 1 allowLatentLinking: {latent_linking_1}")
        print(f"✅ Synapse 2 allowLatentLinking: {latent_linking_2}")
        print(f"✅ Synapse 3 allowLatentLinking: {latent_linking_3}")
        print("✅ Multiple pairings latent linking test completed")
        
    def tearDown(self):
        """Clean up after each test method."""
        print("Cleaning up latent linking test...")
        self.registry = None

if __name__ == '__main__':
    unittest.main(verbosity=2)