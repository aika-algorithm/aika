import unittest

import aika
import aika.fields as af
import aika.network as an

class BuilderTestCase(unittest.TestCase):
    
    def setUp(self):
        """Set up test fixtures before each test method."""
        self.registry = af.TypeRegistry()
        
    def test_neuron_type_builder(self):
        """Test NeuronTypeBuilder creation and basic functionality."""
        print("Testing NeuronTypeBuilder...")
        
        # Create a neuron type builder
        neuron_builder = an.NeuronTypeBuilder(self.registry, "TEST_NEURON")
        
        # Test basic properties
        self.assertIsNotNone(neuron_builder)
        self.assertEqual(str(neuron_builder), "TEST_NEURON")
        
        # Test static relations exist
        self.assertIsNotNone(an.NeuronTypeBuilder.SELF)
        self.assertIsNotNone(an.NeuronTypeBuilder.INPUT)
        self.assertIsNotNone(an.NeuronTypeBuilder.OUTPUT)
        self.assertIsNotNone(an.NeuronTypeBuilder.ACTIVATION)
        
        print("✅ NeuronTypeBuilder test passed")
        
    def test_activation_type_builder(self):
        """Test ActivationTypeBuilder creation and basic functionality."""
        print("Testing ActivationTypeBuilder...")
        
        # Create an activation type builder
        activation_builder = an.ActivationTypeBuilder(self.registry, "TEST_ACTIVATION")
        
        # Test basic properties
        self.assertIsNotNone(activation_builder)
        self.assertEqual(str(activation_builder), "TEST_ACTIVATION")
        
        # Test static relations exist
        self.assertIsNotNone(an.ActivationTypeBuilder.SELF)
        self.assertIsNotNone(an.ActivationTypeBuilder.INPUT)
        self.assertIsNotNone(an.ActivationTypeBuilder.OUTPUT)
        self.assertIsNotNone(an.ActivationTypeBuilder.NEURON)
        
        print("✅ ActivationTypeBuilder test passed")
        
    def test_synapse_type_builder(self):
        """Test SynapseTypeBuilder creation and basic functionality."""
        print("Testing SynapseTypeBuilder...")
        
        # Create a synapse type builder
        synapse_builder = an.SynapseTypeBuilder(self.registry, "TEST_SYNAPSE")
        
        # Test basic properties
        self.assertIsNotNone(synapse_builder)
        self.assertEqual(str(synapse_builder), "TEST_SYNAPSE")
        
        # Test static relations exist
        self.assertIsNotNone(an.SynapseTypeBuilder.SELF)
        self.assertIsNotNone(an.SynapseTypeBuilder.INPUT)
        self.assertIsNotNone(an.SynapseTypeBuilder.OUTPUT)
        self.assertIsNotNone(an.SynapseTypeBuilder.LINK)
        
        print("✅ SynapseTypeBuilder test passed")
        
    def test_link_type_builder(self):
        """Test LinkTypeBuilder creation and basic functionality."""
        print("Testing LinkTypeBuilder...")
        
        # Create a link type builder
        link_builder = an.LinkTypeBuilder(self.registry, "TEST_LINK")
        
        # Test basic properties
        self.assertIsNotNone(link_builder)
        self.assertEqual(str(link_builder), "TEST_LINK")
        
        # Test static relations exist
        self.assertIsNotNone(an.LinkTypeBuilder.SELF)
        self.assertIsNotNone(an.LinkTypeBuilder.INPUT)
        self.assertIsNotNone(an.LinkTypeBuilder.OUTPUT)
        self.assertIsNotNone(an.LinkTypeBuilder.SYNAPSE)
        self.assertIsNotNone(an.LinkTypeBuilder.PAIR_IN)
        self.assertIsNotNone(an.LinkTypeBuilder.PAIR_OUT)
        
        print("✅ LinkTypeBuilder test passed")
        
    def test_builder_configuration(self):
        """Test builder configuration and relationships."""
        print("Testing builder configuration...")
        
        # Create builders
        neuron_builder = an.NeuronTypeBuilder(self.registry, "CONFIG_NEURON")
        activation_builder = an.ActivationTypeBuilder(self.registry, "CONFIG_ACTIVATION")
        
        # Configure relationship
        neuron_builder.setActivation(activation_builder)
        activation_builder.setNeuron(neuron_builder)
        
        # Test getter
        retrieved_activation = neuron_builder.getActivation()
        self.assertIsNotNone(retrieved_activation)
        self.assertEqual(str(retrieved_activation), "CONFIG_ACTIVATION")
        
        retrieved_neuron = activation_builder.getNeuron()
        self.assertIsNotNone(retrieved_neuron)
        self.assertEqual(str(retrieved_neuron), "CONFIG_NEURON")
        
        print("✅ Builder configuration test passed")
        
    def tearDown(self):
        """Clean up after each test method."""
        self.registry = None

if __name__ == '__main__':
    # Run with verbose output to see detailed test progress
    unittest.main(verbosity=2)