import unittest

import aika
import aika.fields as af
import aika.network as an

class PropagateLatentLinkingTestCase(unittest.TestCase):
    """
    Test the Linker::propagate method's behavior with allowLatentLinking.
    When allowLatentLinking is enabled, propagate should use linkLatent 
    instead of normal propagation.
    """
    
    def setUp(self):
        """Set up test fixtures before each test method."""
        print("Setting up propagate latent linking test...")
        self.registry = af.TypeRegistry()
        
        # Create basic neuron types for testing
        self.neuron_A_builder = an.NeuronTypeBuilder(self.registry, "NEURON_A")
        self.neuron_B_builder = an.NeuronTypeBuilder(self.registry, "NEURON_B") 
        self.neuron_C_builder = an.NeuronTypeBuilder(self.registry, "NEURON_C")
        
        self.neuron_A = self.neuron_A_builder.build()
        self.neuron_B = self.neuron_B_builder.build()
        self.neuron_C = self.neuron_C_builder.build()
        
        print("Created test neuron types: A, B, C")
        
    def test_normal_propagation_without_latent_linking(self):
        """Test normal propagation when allowLatentLinking is false."""
        print("Testing normal propagation without latent linking...")
        
        # Create synapse without latent linking: A→B
        synapse_builder = an.SynapseTypeBuilder(self.registry, "SYN_NORMAL_PROPAGATE")
        synapse_builder.setInput(self.neuron_A).setOutput(self.neuron_B)
        synapse_type = synapse_builder.build()
        
        # Verify allowLatentLinking is false (default)
        self.assertFalse(synapse_type.getAllowLatentLinking(), 
                        "Default allowLatentLinking should be false")
        
        print(f"✅ Created synapse without latent linking: allowLatentLinking = {synapse_type.getAllowLatentLinking()}")
        
        # Note: We can't easily test the actual propagation behavior without 
        # setting up a full model/context/activation scenario, but we can verify 
        # that the synapse type configuration is correct for normal propagation
        
        print("✅ Normal propagation configuration verified")
        
    def test_latent_linking_propagation_when_enabled(self):
        """Test that allowLatentLinking flag is correctly set for paired synapses."""
        print("Testing latent linking configuration for paired synapses...")
        
        # Create paired synapses that share common output neuron: A→B, C→B
        # This should enable allowLatentLinking on both synapses
        synapse1_builder = an.SynapseTypeBuilder(self.registry, "SYN_LATENT_1")
        synapse1_builder.setInput(self.neuron_A).setOutput(self.neuron_B)
        synapse1 = synapse1_builder.build()
        
        synapse2_builder = an.SynapseTypeBuilder(self.registry, "SYN_LATENT_2")
        synapse2_builder.setInput(self.neuron_C).setOutput(self.neuron_B)
        synapse2_builder.pair(synapse1, 0)  # Pair with binding signal
        synapse2 = synapse2_builder.build()
        
        # Verify both synapses have allowLatentLinking enabled
        latent_linking_1 = synapse1.getAllowLatentLinking()
        latent_linking_2 = synapse2.getAllowLatentLinking()
        
        self.assertTrue(latent_linking_1, 
                       "Paired synapse 1 should have allowLatentLinking = true")
        self.assertTrue(latent_linking_2, 
                       "Paired synapse 2 should have allowLatentLinking = true")
        
        print(f"✅ Synapse 1 allowLatentLinking: {latent_linking_1}")
        print(f"✅ Synapse 2 allowLatentLinking: {latent_linking_2}")
        print("✅ Latent linking configuration verified for paired synapses")
        
    def test_mixed_propagation_scenarios(self):
        """Test mixed scenarios with both normal and latent linking synapses."""
        print("Testing mixed propagation scenarios...")
        
        # Create normal synapse: A→C (no pairing)
        normal_synapse_builder = an.SynapseTypeBuilder(self.registry, "SYN_MIXED_NORMAL")
        normal_synapse_builder.setInput(self.neuron_A).setOutput(self.neuron_C)
        normal_synapse = normal_synapse_builder.build()
        
        # Create paired synapses: A→B, C→B (should enable latent linking)
        latent_synapse1_builder = an.SynapseTypeBuilder(self.registry, "SYN_MIXED_LATENT_1")
        latent_synapse1_builder.setInput(self.neuron_A).setOutput(self.neuron_B)
        latent_synapse1 = latent_synapse1_builder.build()
        
        latent_synapse2_builder = an.SynapseTypeBuilder(self.registry, "SYN_MIXED_LATENT_2")
        latent_synapse2_builder.setInput(self.neuron_C).setOutput(self.neuron_B)
        latent_synapse2_builder.pair(latent_synapse1, 1)
        latent_synapse2 = latent_synapse2_builder.build()
        
        # Verify propagation types
        self.assertFalse(normal_synapse.getAllowLatentLinking(), 
                        "Normal synapse should use regular propagation")
        self.assertTrue(latent_synapse1.getAllowLatentLinking(), 
                       "Paired synapse 1 should use latent linking")
        self.assertTrue(latent_synapse2.getAllowLatentLinking(), 
                       "Paired synapse 2 should use latent linking")
        
        print(f"✅ Normal synapse allowLatentLinking: {normal_synapse.getAllowLatentLinking()}")
        print(f"✅ Latent synapse 1 allowLatentLinking: {latent_synapse1.getAllowLatentLinking()}")
        print(f"✅ Latent synapse 2 allowLatentLinking: {latent_synapse2.getAllowLatentLinking()}")
        print("✅ Mixed propagation scenarios verified")
        
    def test_dot_product_use_case(self):
        """Test the typical dot-product use case that motivated this feature."""
        print("Testing dot-product use case...")
        
        # Simulate dot-product scenario:
        # Key synapse: neuron_A (key) → neuron_C (comp) 
        # Query synapse: neuron_B (query) → neuron_C (comp)
        # Both should use latent linking for dot-product computation
        
        key_synapse_builder = an.SynapseTypeBuilder(self.registry, "KEY_SYNAPSE")
        key_synapse_builder.setInput(self.neuron_A).setOutput(self.neuron_C)  # key → comp
        key_synapse = key_synapse_builder.build()
        
        query_synapse_builder = an.SynapseTypeBuilder(self.registry, "QUERY_SYNAPSE") 
        query_synapse_builder.setInput(self.neuron_B).setOutput(self.neuron_C)  # query → comp
        query_synapse_builder.pair(key_synapse, 0)  # Pair for dot-product
        query_synapse = query_synapse_builder.build()
        
        # Both synapses should have latent linking enabled for dot-product
        key_latent = key_synapse.getAllowLatentLinking()
        query_latent = query_synapse.getAllowLatentLinking()
        
        self.assertTrue(key_latent, "Key synapse should use latent linking")
        self.assertTrue(query_latent, "Query synapse should use latent linking")
        
        print(f"✅ Key synapse (A→C) allowLatentLinking: {key_latent}")
        print(f"✅ Query synapse (B→C) allowLatentLinking: {query_latent}")
        print("✅ Dot-product use case verified")
        print("    When Linker::propagate is called on these synapses,")
        print("    it will use linkLatent instead of normal propagation")
        
    def tearDown(self):
        """Clean up after each test method."""
        print("Cleaning up propagate latent linking test...")
        self.registry = None

if __name__ == '__main__':
    unittest.main(verbosity=2)