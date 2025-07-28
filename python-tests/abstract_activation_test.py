import unittest
import sys
import os

# Add the project root to Python's module search path
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), "..")))

import aika

class AbstractActivationTest(unittest.TestCase):
    
    def setUp(self):
        """Initialize common test objects"""
        self.type_registry = aika.fields.TypeRegistry()
        self.model = aika.network.Model(self.type_registry)
        
        # Create node definition (equivalent to Java's NodeDefinition)
        self.neuron_def = aika.network.NeuronDefinition(self.type_registry, "test")
        self.activation_def = aika.network.ActivationDefinition(self.type_registry, "test_activation")
        self.neuron_def.setActivation(self.activation_def)
        
        # Initialize flattened types
        self.type_registry.flattenTypeHierarchy()
        
        # Create neuron instance
        self.neuron = self.neuron_def.instantiate(self.model)
        print(f"DEBUG: self.neuron = {self.neuron}")
        
        # Create document
        self.doc = aika.network.Document(self.model)
        
        # Test BSTypes - Create concrete BSType implementations
        self.BSType_A = TestBSType("A")
        self.BSType_B = TestBSType("B")


class TestBSType:
    """Test implementation of BSType"""
    
    def __init__(self, name):
        self.name = name
        
    def __str__(self):
        return self.name
        
    def __repr__(self):
        return f"TestBSType({self.name})"
        
    def __eq__(self, other):
        return isinstance(other, TestBSType) and self.name == other.name
        
    def __hash__(self):
        return hash(self.name)


class TestUtils:
    """Utility functions for testing"""
    
    @staticmethod
    def get_input_link(activation, synapse_id):
        """Get input link by synapse ID"""
        input_links = activation.getInputLinks()
        for link in input_links:
            if link.getSynapse().getSynapseId() == synapse_id:
                return link
        return None