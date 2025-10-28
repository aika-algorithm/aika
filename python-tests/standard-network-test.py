import unittest
import sys
import os

# Add the project root to Python's module search path
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), "..")))

import aika
import aika.fields as af
import aika.network as an
from python.standard_network import create_standard_network_types

class StandardNetworkTestCase(unittest.TestCase):
    
    def setUp(self):
        """Set up test fixtures before each test method."""
        print("Setting up standard network test...")
        self.standard_network = create_standard_network_types()
        
    def test_standard_network_type_creation(self):
        """Test that all standard network types are created successfully."""
        print("Testing standard network type creation...")
        
        # Test that standard_network object exists
        self.assertIsNotNone(self.standard_network)
        
        # Test that registry exists
        registry = self.standard_network.get_registry()
        self.assertIsNotNone(registry)
        
        # Test standard types exist
        self.assertIsNotNone(self.standard_network.T_STANDARD_NEURON)
        self.assertIsNotNone(self.standard_network.T_STANDARD_ACTIVATION)
        self.assertIsNotNone(self.standard_network.T_STANDARD_SYNAPSE)
        self.assertIsNotNone(self.standard_network.T_STANDARD_LINK)
        
    def test_standard_type_names(self):
        """Test that standard types have correct names."""
        print("Testing standard type names...")
        
        # Test type names (some include prefixes in their string representation)
        self.assertEqual(str(self.standard_network.T_STANDARD_NEURON), "STANDARD_NEURON")
        self.assertEqual(str(self.standard_network.T_STANDARD_ACTIVATION), "STANDARD_NEURON")
        self.assertIn("STANDARD_SYNAPSE", str(self.standard_network.T_STANDARD_SYNAPSE))
        self.assertIn("STANDARD_SYNAPSE", str(self.standard_network.T_STANDARD_LINK))
        
    def test_standard_type_getters(self):
        """Test that getter methods work correctly."""
        print("Testing standard type getters...")
        
        # Test getter methods
        self.assertEqual(self.standard_network.get_standard_neuron_type(), self.standard_network.T_STANDARD_NEURON)
        self.assertEqual(self.standard_network.get_standard_activation_type(), self.standard_network.T_STANDARD_ACTIVATION)
        self.assertEqual(self.standard_network.get_standard_synapse_type(), self.standard_network.T_STANDARD_SYNAPSE)
        self.assertEqual(self.standard_network.get_standard_link_type(), self.standard_network.T_STANDARD_LINK)
        
    def test_neuron_activation_relationship(self):
        """Test that neuron-activation relationships are correct."""
        print("Testing neuron-activation relationships...")
        
        # Test that neuron has correct activation type
        self.assertEqual(str(self.standard_network.T_STANDARD_NEURON.getActivationType()), "STANDARD_NEURON")
        
    def test_synapse_link_relationship(self):
        """Test that synapse-link relationships are correct."""
        print("Testing synapse-link relationships...")
        
        # Test that synapse has correct link type
        # Note: toString includes class prefix, so we check contains
        self.assertIn("STANDARD_SYNAPSE", str(self.standard_network.T_STANDARD_SYNAPSE.getLinkType()))
        
    def test_standard_network_instantiation(self):
        """Test that standard types can be instantiated."""
        print("Testing standard network instantiation...")
        
        # Create model and test neuron instantiation
        registry = self.standard_network.get_registry()
        model = an.Model(registry)
        
        # Instantiate standard neuron
        standard_neuron = self.standard_network.T_STANDARD_NEURON.instantiate(model)
        self.assertIsNotNone(standard_neuron)
        print(f"Created standard neuron: {standard_neuron}")
        
        # Test that fields can be set
        bias_field = self.standard_network.T_STANDARD_NEURON.sum("bias")
        standard_neuron.setFieldValue(bias_field, 1.0)
        print("Set bias field value on standard neuron")
        
        # Test that synapse can be instantiated
        standard_synapse = self.standard_network.T_STANDARD_SYNAPSE.instantiate(standard_neuron, standard_neuron)
        self.assertIsNotNone(standard_synapse)
        print(f"Created standard synapse: {standard_synapse}")
        
    def tearDown(self):
        """Clean up after each test method."""
        print("Cleaning up standard network test...")
        self.standard_network = None

if __name__ == '__main__':
    # Run with verbose output to see detailed test progress
    unittest.main(verbosity=2)