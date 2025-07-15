import unittest
import sys
import os

# Add the project root to Python's module search path
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), "..")))

import aika
from abstract_activation_test import AbstractActivationTest, TestBSType, TestUtils

class ConjunctiveActivationTest(AbstractActivationTest):
    
    def setUp(self):
        """Initialize test objects with additional setup for conjunctive activation tests"""
        super().setUp()
        
        # Create input neuron definition
        self.input_neuron_def = aika.network.NeuronDefinition(self.type_registry, "input")
        self.input_activation_def = aika.network.ActivationDefinition(self.type_registry, "input_activation")
        self.input_neuron_def.setActivation(self.input_activation_def)
        
        # Create synapse definition
        self.synapse_def = aika.network.SynapseDefinition(self.type_registry, "test_synapse")
        self.synapse_def.setSubType(aika.network.SynapseSubType.CONJUNCTIVE)
        self.synapse_def.setInput(self.input_neuron_def)
        self.synapse_def.setOutput(self.neuron_def)
        
        # Create link definition
        self.link_def = aika.network.LinkDefinition(self.type_registry, "test_link")
        self.link_def.setInput(self.input_activation_def)
        self.link_def.setOutput(self.activation_def)
        self.link_def.setSynapse(self.synapse_def)
        
        # Set link in synapse definition
        self.synapse_def.setLink(self.link_def)
        
        # Re-flatten type hierarchy after adding new definitions
        self.type_registry.flattenTypeHierarchy()
        
        # Create input neuron instance
        self.input_neuron = self.input_neuron_def.instantiate(self.model)
        
        # Create synapse instance
        self.synapse = self.synapse_def.instantiate(self.input_neuron, self.neuron)
    
    def test_link_incoming(self):
        """Test linkIncoming method"""
        # Create binding signal
        bs0 = aika.network.BindingSignal(0, self.doc)
        
        # Create input activation with binding signal A
        input_binding_signals = {self.BSType_A: bs0}
        input_activation = aika.network.ConjunctiveActivation(
            self.input_activation_def,
            None,  # parent
            self.doc.createActivationId(),
            self.input_neuron,
            self.doc,
            input_binding_signals
        )
        
        # Create output activation with binding signal B (transformed from A)
        output_binding_signals = {self.BSType_B: bs0}
        output_activation = aika.network.ConjunctiveActivation(
            self.activation_def,
            None,  # parent
            self.doc.createActivationId(),
            self.neuron,
            self.doc,
            output_binding_signals
        )
        
        # Initially, no input link should exist
        input_link = TestUtils.get_input_link(output_activation, 0)
        self.assertIsNone(input_link)
        
        # Add input activation to binding signal
        bs0.addActivation(input_activation)
        
        # Still no link should exist until linkIncoming is called
        input_link = TestUtils.get_input_link(output_activation, 0)
        self.assertIsNone(input_link)
        
        # Call linkIncoming to establish the link
        output_activation.linkIncoming(None)
        
        # Now the input link should exist and point to the input activation
        input_link = TestUtils.get_input_link(output_activation, 0)
        self.assertIsNotNone(input_link)
        self.assertEqual(input_activation, input_link.getInput())
    
    def test_add_input_link(self):
        """Test addInputLink method"""
        # Create binding signal
        bs0 = aika.network.BindingSignal(0, self.doc)
        
        # Create input and output activations
        input_activation = aika.network.ConjunctiveActivation(
            self.input_activation_def,
            None,
            self.doc.createActivationId(),
            self.input_neuron,
            self.doc,
            {self.BSType_A: bs0}
        )
        
        output_activation = aika.network.ConjunctiveActivation(
            self.activation_def,
            None,
            self.doc.createActivationId(),
            self.neuron,
            self.doc,
            {self.BSType_B: bs0}
        )
        
        # Create a link between activations
        link = aika.network.Link(
            self.link_def,
            self.synapse,
            input_activation,
            output_activation
        )
        
        # Add the input link
        output_activation.addInputLink(link)
        
        # Verify the link was added
        input_links = output_activation.getInputLinks()
        self.assertIn(link, input_links)
    
    def test_get_input_links(self):
        """Test getInputLinks method"""
        # Create binding signal
        bs0 = aika.network.BindingSignal(0, self.doc)
        
        # Create activations
        input_activation = aika.network.ConjunctiveActivation(
            self.input_activation_def,
            None,
            self.doc.createActivationId(),
            self.input_neuron,
            self.doc,
            {self.BSType_A: bs0}
        )
        
        output_activation = aika.network.ConjunctiveActivation(
            self.activation_def,
            None,
            self.doc.createActivationId(),
            self.neuron,
            self.doc,
            {self.BSType_B: bs0}
        )
        
        # Initially, no input links
        input_links = output_activation.getInputLinks()
        self.assertEqual(0, len(input_links))
        
        # Create and add a link
        link = aika.network.Link(
            self.link_def,
            self.synapse,
            input_activation,
            output_activation
        )
        
        output_activation.addInputLink(link)
        
        # Now there should be one input link
        input_links = output_activation.getInputLinks()
        self.assertEqual(1, len(input_links))
        self.assertEqual(link, input_links[0])


if __name__ == '__main__':
    unittest.main()