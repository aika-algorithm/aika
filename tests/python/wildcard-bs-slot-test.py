import unittest
import sys
import os

# Add the project root to Python's module search path
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), "..")))

import aika
import aika.fields as af
import aika.network as an

class WildcardBSSlotTestCase(unittest.TestCase):
    """
    Test the wildcardBSSlot logic for spanning neuron pairs.
    The wildcardBSSlot should be set on a neuron when synapse pairs span over it,
    meaning one synapse's output connects to another's input through that neuron.
    """
    
    def setUp(self):
        """Set up test fixtures before each test method."""
        print("Setting up wildcard BS slot test...")
        self.registry = af.TypeRegistry()
        
        # Create basic neuron types for testing
        self.neuron_A_builder = an.NeuronTypeBuilder(self.registry, "NEURON_A")
        self.neuron_B_builder = an.NeuronTypeBuilder(self.registry, "NEURON_B") 
        self.neuron_C_builder = an.NeuronTypeBuilder(self.registry, "NEURON_C")
        
        self.neuron_A = self.neuron_A_builder.build()
        self.neuron_B = self.neuron_B_builder.build()
        self.neuron_C = self.neuron_C_builder.build()
        
        print("Created test neuron types: A, B, C")
        
        # Verify initial state (wildcardBSSlot should be -1 for "not set")
        self.assertEqual(self.neuron_A.getWildcardBSSlot(), -1, "Initial wildcardBSSlot should be -1")
        self.assertEqual(self.neuron_B.getWildcardBSSlot(), -1, "Initial wildcardBSSlot should be -1")
        self.assertEqual(self.neuron_C.getWildcardBSSlot(), -1, "Initial wildcardBSSlot should be -1")
        
    def test_spanning_output_to_input(self):
        """Test spanning when first synapse output connects to second synapse input."""
        print("Testing spanning: A→B, B→C (spans over B)...")
        
        # Create synapses: A→B, B→C (should span over neuron B)
        synapse1_builder = an.SynapseTypeBuilder(self.registry, "SYN_A_TO_B_SPAN")
        synapse1_builder.setInput(self.neuron_A).setOutput(self.neuron_B)
        synapse1 = synapse1_builder.build()
        
        synapse2_builder = an.SynapseTypeBuilder(self.registry, "SYN_B_TO_C_SPAN")
        synapse2_builder.setInput(self.neuron_B).setOutput(self.neuron_C)
        
        # Add binding signal pairing (spans over neuron B)
        synapse2_builder.pair(synapse1, 5)  # Use slot 5
        synapse2 = synapse2_builder.build()
        
        print("✅ Created spanning synapses: A→B, B→C")
        
        # Check wildcardBSSlot results
        wildcard_A = self.neuron_A.getWildcardBSSlot()
        wildcard_B = self.neuron_B.getWildcardBSSlot() 
        wildcard_C = self.neuron_C.getWildcardBSSlot()
        
        # Only neuron B should have wildcardBSSlot set (it's the spanning neuron)
        self.assertEqual(wildcard_A, -1, "Neuron A should not have wildcardBSSlot set")
        self.assertEqual(wildcard_B, 5, "Neuron B should have wildcardBSSlot = 5 (spanning neuron)")
        self.assertEqual(wildcard_C, -1, "Neuron C should not have wildcardBSSlot set")
        
        print(f"✅ Neuron A wildcardBSSlot: {wildcard_A}")
        print(f"✅ Neuron B wildcardBSSlot: {wildcard_B} (spanning neuron)")
        print(f"✅ Neuron C wildcardBSSlot: {wildcard_C}")
        print("✅ Output-to-input spanning test completed")
        
    def test_spanning_input_from_output(self):
        """Test spanning when first synapse input connects from second synapse output."""
        print("Testing spanning: C→A, A→B (spans over A)...")
        
        # Create synapses: C→A, A→B (should span over neuron A)
        synapse1_builder = an.SynapseTypeBuilder(self.registry, "SYN_C_TO_A_SPAN")
        synapse1_builder.setInput(self.neuron_C).setOutput(self.neuron_A)
        synapse1 = synapse1_builder.build()
        
        synapse2_builder = an.SynapseTypeBuilder(self.registry, "SYN_A_TO_B_SPAN2")
        synapse2_builder.setInput(self.neuron_A).setOutput(self.neuron_B)
        
        # Add binding signal pairing (spans over neuron A)
        synapse2_builder.pair(synapse1, 3)  # Use slot 3
        synapse2 = synapse2_builder.build()
        
        print("✅ Created spanning synapses: C→A, A→B")
        
        # Check wildcardBSSlot results
        wildcard_A = self.neuron_A.getWildcardBSSlot()
        wildcard_B = self.neuron_B.getWildcardBSSlot()
        wildcard_C = self.neuron_C.getWildcardBSSlot()
        
        # Only neuron A should have wildcardBSSlot set (it's the spanning neuron)
        self.assertEqual(wildcard_A, 3, "Neuron A should have wildcardBSSlot = 3 (spanning neuron)")
        self.assertEqual(wildcard_B, -1, "Neuron B should not have wildcardBSSlot set")
        self.assertEqual(wildcard_C, -1, "Neuron C should not have wildcardBSSlot set")
        
        print(f"✅ Neuron A wildcardBSSlot: {wildcard_A} (spanning neuron)")
        print(f"✅ Neuron B wildcardBSSlot: {wildcard_B}")
        print(f"✅ Neuron C wildcardBSSlot: {wildcard_C}")
        print("✅ Input-from-output spanning test completed")
        
    def test_no_spanning_parallel_synapses(self):
        """Test that parallel synapses (same input/output) do not set wildcardBSSlot."""
        print("Testing no spanning: A→B, A→B (parallel, no spanning)...")
        
        # Create parallel synapses: A→B, A→B (no spanning)
        synapse1_builder = an.SynapseTypeBuilder(self.registry, "SYN_A_TO_B_PAR1")
        synapse1_builder.setInput(self.neuron_A).setOutput(self.neuron_B)
        synapse1 = synapse1_builder.build()
        
        synapse2_builder = an.SynapseTypeBuilder(self.registry, "SYN_A_TO_B_PAR2")
        synapse2_builder.setInput(self.neuron_A).setOutput(self.neuron_B)
        
        # Add binding signal pairing (no spanning should occur)
        synapse2_builder.pair(synapse1, 7)
        synapse2 = synapse2_builder.build()
        
        print("✅ Created parallel synapses: A→B, A→B")
        
        # Check wildcardBSSlot results - none should be set
        wildcard_A = self.neuron_A.getWildcardBSSlot()
        wildcard_B = self.neuron_B.getWildcardBSSlot()
        wildcard_C = self.neuron_C.getWildcardBSSlot()
        
        # No neurons should have wildcardBSSlot set (no spanning)
        self.assertEqual(wildcard_A, -1, "Neuron A should not have wildcardBSSlot set")
        self.assertEqual(wildcard_B, -1, "Neuron B should not have wildcardBSSlot set") 
        self.assertEqual(wildcard_C, -1, "Neuron C should not have wildcardBSSlot set")
        
        print(f"✅ Neuron A wildcardBSSlot: {wildcard_A}")
        print(f"✅ Neuron B wildcardBSSlot: {wildcard_B}")
        print(f"✅ Neuron C wildcardBSSlot: {wildcard_C}")
        print("✅ No spanning test completed")
        
    def test_non_binding_signal_pairing_no_wildcard(self):
        """Test that BY_SYNAPSE pairing does not set wildcardBSSlot even when spanning."""
        print("Testing BY_SYNAPSE pairing: A→B, B→C (spanning but no binding signal)...")
        
        # Create spanning synapses: A→B, B→C but use BY_SYNAPSE pairing
        synapse1_builder = an.SynapseTypeBuilder(self.registry, "SYN_A_TO_B_NOSIG")
        synapse1_builder.setInput(self.neuron_A).setOutput(self.neuron_B)
        synapse1 = synapse1_builder.build()
        
        synapse2_builder = an.SynapseTypeBuilder(self.registry, "SYN_B_TO_C_NOSIG") 
        synapse2_builder.setInput(self.neuron_B).setOutput(self.neuron_C)
        
        # Add synapse pairing (not binding signal pairing)
        synapse2_builder.pair(synapse1)
        synapse2 = synapse2_builder.build()
        
        print("✅ Created spanning synapses with BY_SYNAPSE pairing")
        
        # Check wildcardBSSlot results - none should be set
        wildcard_A = self.neuron_A.getWildcardBSSlot()
        wildcard_B = self.neuron_B.getWildcardBSSlot()
        wildcard_C = self.neuron_C.getWildcardBSSlot()
        
        # No wildcardBSSlot should be set (only BY_BINDING_SIGNAL pairing sets it)
        self.assertEqual(wildcard_A, -1, "Neuron A should not have wildcardBSSlot set")
        self.assertEqual(wildcard_B, -1, "Neuron B should not have wildcardBSSlot set")
        self.assertEqual(wildcard_C, -1, "Neuron C should not have wildcardBSSlot set")
        
        print(f"✅ Neuron A wildcardBSSlot: {wildcard_A}")
        print(f"✅ Neuron B wildcardBSSlot: {wildcard_B}")
        print(f"✅ Neuron C wildcardBSSlot: {wildcard_C}")
        print("✅ BY_SYNAPSE pairing test completed")
        
    def test_multiple_spanning_pairs(self):
        """Test multiple spanning pairs with different binding signal slots."""
        print("Testing multiple spanning pairs...")
        
        # Create first spanning pair: A→B, B→C (slot 2)
        synapse1_builder = an.SynapseTypeBuilder(self.registry, "SYN_A_TO_B_MULTI1")
        synapse1_builder.setInput(self.neuron_A).setOutput(self.neuron_B)
        synapse1 = synapse1_builder.build()
        
        synapse2_builder = an.SynapseTypeBuilder(self.registry, "SYN_B_TO_C_MULTI1")
        synapse2_builder.setInput(self.neuron_B).setOutput(self.neuron_C)
        synapse2_builder.pair(synapse1, 2)
        synapse2 = synapse2_builder.build()
        
        # Create second spanning pair that also spans over B: C→B, B→A (slot 8)
        # This should overwrite the previous wildcardBSSlot on neuron B
        synapse3_builder = an.SynapseTypeBuilder(self.registry, "SYN_C_TO_B_MULTI2")
        synapse3_builder.setInput(self.neuron_C).setOutput(self.neuron_B)
        synapse3 = synapse3_builder.build()
        
        synapse4_builder = an.SynapseTypeBuilder(self.registry, "SYN_B_TO_A_MULTI2")
        synapse4_builder.setInput(self.neuron_B).setOutput(self.neuron_A)
        synapse4_builder.pair(synapse3, 8)
        synapse4 = synapse4_builder.build()
        
        print("✅ Created multiple spanning synapse pairs")
        
        # Check wildcardBSSlot results
        wildcard_A = self.neuron_A.getWildcardBSSlot()
        wildcard_B = self.neuron_B.getWildcardBSSlot()
        wildcard_C = self.neuron_C.getWildcardBSSlot()
        
        # Neuron B should have the last set wildcardBSSlot value (8)
        self.assertEqual(wildcard_A, -1, "Neuron A should not have wildcardBSSlot set")
        self.assertEqual(wildcard_B, 8, "Neuron B should have wildcardBSSlot = 8 (last spanning)")
        self.assertEqual(wildcard_C, -1, "Neuron C should not have wildcardBSSlot set")
        
        print(f"✅ Neuron A wildcardBSSlot: {wildcard_A}")
        print(f"✅ Neuron B wildcardBSSlot: {wildcard_B} (overwritten to last value)")
        print(f"✅ Neuron C wildcardBSSlot: {wildcard_C}")
        print("✅ Multiple spanning pairs test completed")
        
    def tearDown(self):
        """Clean up after each test method."""
        print("Cleaning up wildcard BS slot test...")
        self.registry = None

if __name__ == '__main__':
    unittest.main(verbosity=2)